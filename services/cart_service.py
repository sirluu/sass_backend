import uuid

from extensions import db
from models import Cart, Product
from utils.tenant_context import get_current_tenant_id


class CartService:
    def add_to_cart(
        self, user_id: str, product_id: str, quantity: int = 1, tenant_id: str = None
    ):
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            if not resolved_tenant_id:
                return {"success": False, "message": "Tenant context is required"}

            product = Product.query.filter_by(
                id=product_id, tenant_id=resolved_tenant_id
            ).first()
            if not product:
                return {"success": False, "message": "Product not found"}

            cart_item = Cart.query.filter_by(
                user_id=user_id, product_id=product_id, tenant_id=resolved_tenant_id
            ).first()

            if cart_item:
                cart_item.quantity += quantity
                cart_item.updated_at = db.func.now()
            else:
                cart_item = Cart(
                    id=str(uuid.uuid4()),
                    tenant_id=resolved_tenant_id,
                    user_id=user_id,
                    product_id=product_id,
                    quantity=quantity,
                )
                db.session.add(cart_item)

            db.session.commit()
            return {
                "success": True,
                "message": "Product added to cart",
                "cart_item": cart_item.to_dict(),
            }

        except Exception as e:
            db.session.rollback()
            return {"success": False, "message": f"Error adding to cart: {str(e)}"}

    def get_cart(self, user_id: str, tenant_id: str = None):
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            query = Cart.query.filter_by(user_id=user_id)
            if resolved_tenant_id:
                query = query.filter_by(tenant_id=resolved_tenant_id)

            cart_items = query.all()
            cart_data = []
            for item in cart_items:
                product = Product.query.filter_by(
                    id=item.product_id, tenant_id=item.tenant_id
                ).first()
                if product:
                    cart_data.append({**item.to_dict(), "product": product.to_dict()})
            return cart_data
        except Exception:
            return []

    def remove_from_cart(self, user_id: str, item_id: str, tenant_id: str = None):
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            query = Cart.query.filter_by(id=item_id, user_id=user_id)
            if resolved_tenant_id:
                query = query.filter_by(tenant_id=resolved_tenant_id)
            cart_item = query.first()
            if cart_item:
                db.session.delete(cart_item)
                db.session.commit()
                return {"success": True, "message": "Item removed from cart"}
            return {"success": False, "message": "Item not found in cart"}
        except Exception as e:
            db.session.rollback()
            return {"success": False, "message": f"Error removing item: {str(e)}"}

    def update_cart_quantity(
        self, user_id: str, item_id: str, quantity: int, tenant_id: str = None
    ):
        try:
            if quantity <= 0:
                return self.remove_from_cart(user_id, item_id, tenant_id)

            resolved_tenant_id = tenant_id or get_current_tenant_id()
            query = Cart.query.filter_by(id=item_id, user_id=user_id)
            if resolved_tenant_id:
                query = query.filter_by(tenant_id=resolved_tenant_id)
            cart_item = query.first()
            if cart_item:
                cart_item.quantity = quantity
                cart_item.updated_at = db.func.now()
                db.session.commit()
                return {
                    "success": True,
                    "message": "Cart updated",
                    "cart_item": cart_item.to_dict(),
                }
            return {"success": False, "message": "Item not found in cart"}
        except Exception as e:
            db.session.rollback()
            return {"success": False, "message": f"Error updating cart: {str(e)}"}

    def clear_cart(self, user_id: str, tenant_id: str = None):
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            query = Cart.query.filter_by(user_id=user_id)
            if resolved_tenant_id:
                query = query.filter_by(tenant_id=resolved_tenant_id)
            query.delete()
            db.session.commit()
            return {"success": True, "message": "Cart cleared"}
        except Exception as e:
            db.session.rollback()
            return {"success": False, "message": f"Error clearing cart: {str(e)}"}

    def get_cart_total(self, user_id: str, tenant_id: str = None):
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            query = Cart.query.filter_by(user_id=user_id)
            if resolved_tenant_id:
                query = query.filter_by(tenant_id=resolved_tenant_id)
            cart_items = query.all()
            total = 0
            item_count = 0
            for item in cart_items:
                product = Product.query.filter_by(
                    id=item.product_id, tenant_id=item.tenant_id
                ).first()
                if product:
                    total += product.price * item.quantity
                    item_count += item.quantity
            return {
                "success": True,
                "total": total,
                "item_count": item_count,
                "formatted_total": f"{total:,.0f}đ",
            }
        except Exception as e:
            return {"success": False, "message": f"Error calculating total: {str(e)}"}
