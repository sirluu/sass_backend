import uuid

from extensions import db
from models import Product, UserLike
from utils.tenant_context import get_current_tenant_id


class LikeService:
    def toggle_like(self, user_id: str, product_id: str, tenant_id: str = None):
        resolved_tenant_id = tenant_id or get_current_tenant_id()
        if not resolved_tenant_id:
            return False, "Tenant context is required"

        product = Product.query.filter_by(
            id=product_id, tenant_id=resolved_tenant_id
        ).first()
        if not product:
            return False, "Product not found"

        existing_like = UserLike.query.filter_by(
            user_id=user_id, product_id=product_id, tenant_id=resolved_tenant_id
        ).first()

        if existing_like:
            db.session.delete(existing_like)
            db.session.commit()
            return False, "Product unliked"

        new_like = UserLike(
            id=str(uuid.uuid4()),
            tenant_id=resolved_tenant_id,
            user_id=user_id,
            product_id=product_id,
        )
        db.session.add(new_like)
        db.session.commit()
        return True, "Product liked"

    def get_user_likes(self, user_id: str, tenant_id: str = None):
        resolved_tenant_id = tenant_id or get_current_tenant_id()
        query = UserLike.query.filter_by(user_id=user_id)
        if resolved_tenant_id:
            query = query.filter_by(tenant_id=resolved_tenant_id)

        likes = query.all()
        result = []
        for like in likes:
            product = Product.query.filter_by(
                id=like.product_id, tenant_id=like.tenant_id
            ).first()
            result.append(
                {
                    **like.to_dict(),
                    "product": product.to_dict() if product else None,
                }
            )
        return result

    def is_liked_by_user(self, user_id: str, product_id: str, tenant_id: str = None):
        resolved_tenant_id = tenant_id or get_current_tenant_id()
        query = UserLike.query.filter_by(user_id=user_id, product_id=product_id)
        if resolved_tenant_id:
            query = query.filter_by(tenant_id=resolved_tenant_id)
        return query.first() is not None

    def get_product_likes_count(self, product_id: str, tenant_id: str = None):
        resolved_tenant_id = tenant_id or get_current_tenant_id()
        query = UserLike.query.filter_by(product_id=product_id)
        if resolved_tenant_id:
            query = query.filter_by(tenant_id=resolved_tenant_id)
        return query.count()

    def get_popular_products(self, limit: int = 10, tenant_id: str = None):
        from sqlalchemy import func

        resolved_tenant_id = tenant_id or get_current_tenant_id()
        query = db.session.query(
            UserLike.product_id, func.count(UserLike.id).label("likes_count")
        )
        if resolved_tenant_id:
            query = query.filter(UserLike.tenant_id == resolved_tenant_id)

        popular_products = (
            query.group_by(UserLike.product_id)
            .order_by(func.count(UserLike.id).desc())
            .limit(limit)
            .all()
        )

        result = []
        for product_id, likes_count in popular_products:
            product = Product.query.get(product_id)
            if product:
                product_dict = product.to_dict()
                product_dict["likes_count"] = likes_count
                result.append(product_dict)
        return result
