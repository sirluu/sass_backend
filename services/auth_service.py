import logging
import uuid
from datetime import datetime
from typing import Any, Dict, Optional

from flask_jwt_extended import create_access_token, create_refresh_token
from werkzeug.security import generate_password_hash

from extensions import db
from models.tenant import Tenant
from models.user import User
from utils.tenant_context import get_current_tenant_id

logger = logging.getLogger(__name__)


class AuthService:
    """Service for authentication and user management."""

    @staticmethod
    def _create_tokens(user: User) -> Dict[str, str]:
        additional_claims = {
            "role": user.role,
            "tenant_id": user.tenant_id,
        }
        access_token = create_access_token(
            identity=user.id, additional_claims=additional_claims
        )
        refresh_token = create_refresh_token(
            identity=user.id, additional_claims=additional_claims
        )
        return {"access_token": access_token, "refresh_token": refresh_token}

    @staticmethod
    def register_user(
        name: str, email: str, password: str, tenant_id: str = None
    ) -> Dict[str, Any]:
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            if not resolved_tenant_id:
                return {
                    "success": False,
                    "message": "Tenant context is required for registration",
                }

            tenant = Tenant.query.get(resolved_tenant_id)
            if not tenant or not tenant.is_active:
                return {"success": False, "message": "Invalid or inactive store"}

            existing_user = User.query.filter_by(
                email=email, tenant_id=resolved_tenant_id
            ).first()
            if existing_user:
                return {
                    "success": False,
                    "message": "User with this email already exists in this store",
                }

            user_id = str(uuid.uuid4())
            user = User(
                id=user_id,
                email=email,
                name=name,
                password=password,
                tenant_id=resolved_tenant_id,
                role="customer",
            )

            db.session.add(user)
            db.session.commit()

            tokens = AuthService._create_tokens(user)
            logger.info(f"User registered: {email} (tenant: {resolved_tenant_id})")

            return {
                "success": True,
                "message": "User registered successfully",
                "user": user.to_dict(),
                **tokens,
            }

        except Exception as e:
            logger.error(f"Error registering user: {str(e)}")
            db.session.rollback()
            return {"success": False, "message": "Registration failed"}

    @staticmethod
    def login_user(
        email: str, password: str, tenant_id: str = None
    ) -> Dict[str, Any]:
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()

            query = User.query.filter_by(email=email)
            if resolved_tenant_id:
                query = query.filter_by(tenant_id=resolved_tenant_id)

            user = query.first()

            if not user or not user.check_password(password):
                return {"success": False, "message": "Invalid email or password"}

            if not user.is_active:
                return {"success": False, "message": "Account is deactivated"}

            if user.role != "super_admin" and resolved_tenant_id:
                if user.tenant_id != resolved_tenant_id:
                    return {
                        "success": False,
                        "message": "User does not belong to this store",
                    }

            tokens = AuthService._create_tokens(user)
            user.updated_at = datetime.utcnow()

            db.session.commit()

            logger.info(f"User logged in: {email}")

            return {
                "success": True,
                "message": "Login successful",
                "user": user.to_dict(),
                **tokens,
            }

        except Exception as e:
            logger.error(f"Error logging in user: {str(e)}")
            return {"success": False, "message": "Login failed"}

    @staticmethod
    def get_user_by_id(user_id: str) -> Optional[User]:
        try:
            return User.query.get(user_id)
        except Exception as e:
            logger.error(f"Error getting user by ID: {str(e)}")
            return None

    @staticmethod
    def update_user_preferences(
        user_id: str, preferences: Dict[str, Any]
    ) -> Dict[str, Any]:
        try:
            user = User.query.get(user_id)
            if not user:
                return {"success": False, "message": "User not found"}

            user.set_preferences(preferences)
            db.session.commit()

            return {
                "success": True,
                "message": "Preferences updated successfully",
                "user": user.to_dict(),
            }

        except Exception as e:
            logger.error(f"Error updating user preferences: {str(e)}")
            db.session.rollback()
            return {"success": False, "message": "Failed to update preferences"}

    @staticmethod
    def refresh_token(current_user_id: str) -> Dict[str, Any]:
        try:
            user = User.query.get(current_user_id)
            if not user or not user.is_active:
                return {"success": False, "message": "Invalid user"}

            additional_claims = {
                "role": user.role,
                "tenant_id": user.tenant_id,
            }
            access_token = create_access_token(
                identity=current_user_id, additional_claims=additional_claims
            )

            return {"success": True, "access_token": access_token}

        except Exception as e:
            logger.error(f"Error refreshing token: {str(e)}")
            return {"success": False, "message": "Token refresh failed"}

    @staticmethod
    def deactivate_user(user_id: str) -> Dict[str, Any]:
        try:
            user = User.query.get(user_id)
            if not user:
                return {"success": False, "message": "User not found"}

            user.is_active = False
            user.updated_at = datetime.utcnow()

            db.session.commit()

            return {"success": True, "message": "User account deactivated"}

        except Exception as e:
            logger.error(f"Error deactivating user: {str(e)}")
            db.session.rollback()
            return {"success": False, "message": "Failed to deactivate user"}

    @staticmethod
    def create_store_admin(
        tenant_id: str, name: str, email: str, password: str
    ) -> Dict[str, Any]:
        try:
            tenant = Tenant.query.get(tenant_id)
            if not tenant:
                return {"success": False, "message": "Store not found"}

            existing = User.query.filter_by(email=email, tenant_id=tenant_id).first()
            if existing:
                return {"success": False, "message": "Admin already exists for this store"}

            user = User(
                id=str(uuid.uuid4()),
                email=email,
                name=name,
                password=password,
                tenant_id=tenant_id,
                role="store_admin",
            )

            db.session.add(user)
            db.session.commit()

            return {
                "success": True,
                "message": "Store admin created",
                "user": user.to_dict(),
            }

        except Exception as e:
            logger.error(f"Error creating store admin: {str(e)}")
            db.session.rollback()
            return {"success": False, "message": "Failed to create store admin"}
