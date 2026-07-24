from flask import Blueprint, jsonify, request
from flask_jwt_extended import get_jwt, get_jwt_identity, jwt_required

from services.cart_service import CartService
from utils.decorators import tenant_required
from utils.tenant_context import get_current_tenant_id

cart_bp = Blueprint("cart", __name__)
cart_service = CartService()


def _ensure_user_access(user_id: str):
    current_user = get_jwt_identity()
    if current_user != user_id:
        return jsonify({"success": False, "message": "Unauthorized"}), 403
    return None


def _ensure_tenant_access():
    claims = get_jwt()
    role = claims.get("role", "customer")
    tenant_id = get_current_tenant_id()

    if role == "super_admin":
        return None

    if role != "super_admin" and claims.get("tenant_id") != tenant_id:
        return jsonify(
            {"success": False, "message": "Access denied for this store"}
        ), 403
    return None


@cart_bp.route("/cart/<user_id>", methods=["GET"])
@jwt_required()
@tenant_required
def get_cart(user_id):
    try:
        access_error = _ensure_user_access(user_id) or _ensure_tenant_access()
        if access_error:
            return access_error

        cart_items = cart_service.get_cart(user_id)
        return jsonify({"success": True, "items": cart_items}), 200
    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500


@cart_bp.route("/cart/add", methods=["POST"])
@jwt_required()
@tenant_required
def add_to_cart():
    try:
        current_user = get_jwt_identity()
        data = request.get_json() or {}
        user_id = data.get("user_id")
        product_id = data.get("product_id")
        quantity = data.get("quantity", 1)

        access_error = _ensure_user_access(user_id) or _ensure_tenant_access()
        if access_error:
            return access_error

        if not user_id or not product_id:
            return jsonify(
                {"success": False, "message": "user_id and product_id are required"}
            ), 400

        result = cart_service.add_to_cart(user_id, product_id, quantity)
        status = 200 if result.get("success") else 400
        return jsonify(result), status
    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500


@cart_bp.route("/cart/remove", methods=["DELETE"])
@jwt_required()
@tenant_required
def remove_from_cart():
    try:
        data = request.get_json() or {}
        user_id = data.get("user_id")
        item_id = data.get("item_id")

        access_error = _ensure_user_access(user_id) or _ensure_tenant_access()
        if access_error:
            return access_error

        if not user_id or not item_id:
            return jsonify(
                {"success": False, "message": "user_id and item_id are required"}
            ), 400

        result = cart_service.remove_from_cart(user_id, item_id)
        status = 200 if result.get("success") else 404
        return jsonify(result), status
    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500


@cart_bp.route("/cart/update", methods=["PUT"])
@jwt_required()
@tenant_required
def update_cart_quantity():
    try:
        data = request.get_json() or {}
        user_id = data.get("user_id")
        item_id = data.get("item_id")
        quantity = data.get("quantity")

        access_error = _ensure_user_access(user_id) or _ensure_tenant_access()
        if access_error:
            return access_error

        if not user_id or not item_id or quantity is None:
            return jsonify(
                {
                    "success": False,
                    "message": "user_id, item_id, and quantity are required",
                }
            ), 400

        result = cart_service.update_cart_quantity(user_id, item_id, quantity)
        status = 200 if result.get("success") else 404
        return jsonify(result), status
    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500


@cart_bp.route("/cart/clear", methods=["DELETE"])
@jwt_required()
@tenant_required
def clear_cart():
    try:
        data = request.get_json() or {}
        user_id = data.get("user_id")

        access_error = _ensure_user_access(user_id) or _ensure_tenant_access()
        if access_error:
            return access_error

        if not user_id:
            return jsonify({"success": False, "message": "user_id is required"}), 400

        result = cart_service.clear_cart(user_id)
        status = 200 if result.get("success") else 500
        return jsonify(result), status
    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 500
