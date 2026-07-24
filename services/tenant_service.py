import logging
import uuid
from typing import Any, Dict, List, Optional

from extensions import db
from models.tenant import Tenant
from models.user import User
from services.auth_service import AuthService

logger = logging.getLogger(__name__)


class TenantService:
    @staticmethod
    def get_by_slug(slug: str) -> Optional[Tenant]:
        return Tenant.query.filter_by(slug=slug, is_active=True).first()

    @staticmethod
    def get_by_domain(domain: str) -> Optional[Tenant]:
        return Tenant.query.filter_by(domain=domain, is_active=True).first()

    @staticmethod
    def get_by_id(tenant_id: str) -> Optional[Tenant]:
        return Tenant.query.get(tenant_id)

    @staticmethod
    def list_tenants(include_inactive: bool = False) -> List[Tenant]:
        query = Tenant.query
        if not include_inactive:
            query = query.filter_by(is_active=True)
        return query.order_by(Tenant.name).all()

    @staticmethod
    def create_tenant(data: Dict[str, Any]) -> Dict[str, Any]:
        try:
            slug = data.get("slug", "").strip().lower()
            if not slug or not data.get("name"):
                return {"success": False, "message": "Name and slug are required"}

            if Tenant.query.filter_by(slug=slug).first():
                return {"success": False, "message": "Slug already exists"}

            domain = data.get("domain")
            if domain and Tenant.query.filter_by(domain=domain).first():
                return {"success": False, "message": "Domain already in use"}

            tenant = Tenant(
                id=str(uuid.uuid4()),
                name=data["name"],
                slug=slug,
                domain=domain,
                logo_url=data.get("logoUrl") or data.get("logo_url"),
                description=data.get("description"),
                is_active=data.get("isActive", True),
            )
            if data.get("settings"):
                tenant.set_settings(data["settings"])

            db.session.add(tenant)
            db.session.commit()

            admin_result = None
            if data.get("adminEmail") and data.get("adminPassword"):
                admin_result = AuthService.create_store_admin(
                    tenant.id,
                    data.get("adminName", "Store Admin"),
                    data["adminEmail"],
                    data["adminPassword"],
                )

            return {
                "success": True,
                "message": "Store created successfully",
                "tenant": tenant.to_dict(include_settings=True),
                "admin": admin_result.get("user") if admin_result else None,
            }

        except Exception as e:
            logger.error(f"Error creating tenant: {e}")
            db.session.rollback()
            return {"success": False, "message": "Failed to create store"}

    @staticmethod
    def update_tenant(tenant_id: str, data: Dict[str, Any]) -> Dict[str, Any]:
        try:
            tenant = Tenant.query.get(tenant_id)
            if not tenant:
                return {"success": False, "message": "Store not found"}

            if "name" in data:
                tenant.name = data["name"]
            if "domain" in data:
                domain = data["domain"]
                if domain:
                    existing = Tenant.query.filter_by(domain=domain).first()
                    if existing and existing.id != tenant_id:
                        return {"success": False, "message": "Domain already in use"}
                tenant.domain = domain
            if "logoUrl" in data or "logo_url" in data:
                tenant.logo_url = data.get("logoUrl") or data.get("logo_url")
            if "description" in data:
                tenant.description = data["description"]
            if "isActive" in data:
                tenant.is_active = data["isActive"]
            if "settings" in data:
                tenant.set_settings(data["settings"])

            db.session.commit()

            return {
                "success": True,
                "message": "Store updated successfully",
                "tenant": tenant.to_dict(include_settings=True),
            }

        except Exception as e:
            logger.error(f"Error updating tenant: {e}")
            db.session.rollback()
            return {"success": False, "message": "Failed to update store"}

    @staticmethod
    def get_tenant_stats(tenant_id: str) -> Dict[str, Any]:
        from models.product import Product
        from models.user import User
        from sqlalchemy import func

        return {
            "productCount": Product.query.filter_by(
                tenant_id=tenant_id, is_active=True
            ).count(),
            "customerCount": User.query.filter_by(
                tenant_id=tenant_id, role="customer"
            ).count(),
            "inStockCount": Product.query.filter(
                Product.tenant_id == tenant_id,
                Product.is_active == True,
                Product.stock > 0,
            ).count(),
            "categoryCount": Product.query.with_entities(
                func.count(func.distinct(Product.category))
            )
            .filter(Product.tenant_id == tenant_id, Product.is_active == True)
            .scalar()
            or 0,
        }
