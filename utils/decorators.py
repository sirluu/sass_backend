from functools import wraps

from flask import g, jsonify
from flask_jwt_extended import get_jwt, jwt_required

from utils.tenant_context import get_current_tenant_id


def tenant_required(fn):
    """Require a resolved tenant in request context."""

    @wraps(fn)
    def wrapper(*args, **kwargs):
        if not get_current_tenant_id():
            return jsonify(
                {"success": False, "message": "Tenant context is required"}
            ), 400
        return fn(*args, **kwargs)

    return wrapper


def store_admin_required(fn):
    """Require store_admin or super_admin role with tenant scope."""

    @wraps(fn)
    @jwt_required()
    def wrapper(*args, **kwargs):
        claims = get_jwt()
        role = claims.get("role", "customer")

        if role not in ("store_admin", "super_admin"):
            return jsonify(
                {"success": False, "message": "Admin access required"}
            ), 403

        if role == "store_admin":
            tenant_id = get_current_tenant_id()
            if not tenant_id:
                return jsonify(
                    {"success": False, "message": "Tenant context is required"}
                ), 400
            if claims.get("tenant_id") != tenant_id:
                return jsonify(
                    {"success": False, "message": "Access denied for this store"}
                ), 403

        return fn(*args, **kwargs)

    return wrapper


def super_admin_required(fn):
    """Require platform super_admin role."""

    @wraps(fn)
    @jwt_required()
    def wrapper(*args, **kwargs):
        claims = get_jwt()
        if claims.get("role") != "super_admin":
            return jsonify(
                {"success": False, "message": "Super admin access required"}
            ), 403
        return fn(*args, **kwargs)

    return wrapper
