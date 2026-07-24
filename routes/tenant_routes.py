import logging

from flask import Blueprint, jsonify, request
from flask_jwt_extended import get_jwt, jwt_required

from services.tenant_service import TenantService
from utils.decorators import store_admin_required, super_admin_required
from utils.tenant_context import get_current_tenant, get_current_tenant_id

logger = logging.getLogger(__name__)
tenant_bp = Blueprint("tenants", __name__)


@tenant_bp.route("/resolve", methods=["GET"])
def resolve_tenant():
    """Resolve store by slug or domain (public)."""
    try:
        slug = request.args.get("slug")
        domain = request.args.get("domain")

        tenant = None
        if slug:
            tenant = TenantService.get_by_slug(slug)
        elif domain:
            tenant = TenantService.get_by_domain(domain)

        if not tenant:
            return jsonify({"success": False, "message": "Store not found"}), 404

        return jsonify({"success": True, "tenant": tenant.to_dict(include_settings=True)}), 200

    except Exception as e:
        logger.error(f"Error resolving tenant: {e}")
        return jsonify({"success": False, "message": "Failed to resolve store"}), 500


@tenant_bp.route("/current", methods=["GET"])
def get_current_store():
    """Get store from request context."""
    tenant = get_current_tenant()
    if not tenant:
        return jsonify({"success": False, "message": "Store context not found"}), 404
    return jsonify({"success": True, "tenant": tenant.to_dict(include_settings=True)}), 200


@tenant_bp.route("/", methods=["GET"])
@super_admin_required
def list_tenants():
    try:
        tenants = TenantService.list_tenants()
        return jsonify(
            {
                "success": True,
                "tenants": [t.to_dict() for t in tenants],
                "count": len(tenants),
            }
        ), 200
    except Exception as e:
        logger.error(f"Error listing tenants: {e}")
        return jsonify({"success": False, "message": "Failed to list stores"}), 500


@tenant_bp.route("/", methods=["POST"])
@super_admin_required
def create_tenant():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"success": False, "message": "Request body required"}), 400

        result = TenantService.create_tenant(data)
        status = 201 if result["success"] else 400
        return jsonify(result), status

    except Exception as e:
        logger.error(f"Error creating tenant: {e}")
        return jsonify({"success": False, "message": "Failed to create store"}), 500


@tenant_bp.route("/<tenant_id>", methods=["GET"])
@jwt_required()
def get_tenant(tenant_id):
    try:
        claims = get_jwt()
        role = claims.get("role", "customer")

        if role == "super_admin" or (
            role == "store_admin" and claims.get("tenant_id") == tenant_id
        ):
            tenant = TenantService.get_by_id(tenant_id)
            if not tenant:
                return jsonify({"success": False, "message": "Store not found"}), 404
            stats = TenantService.get_tenant_stats(tenant_id)
            return jsonify(
                {
                    "success": True,
                    "tenant": tenant.to_dict(include_settings=True),
                    "stats": stats,
                }
            ), 200

        return jsonify({"success": False, "message": "Access denied"}), 403

    except Exception as e:
        logger.error(f"Error getting tenant: {e}")
        return jsonify({"success": False, "message": "Failed to get store"}), 500


@tenant_bp.route("/<tenant_id>", methods=["PUT"])
@store_admin_required
def update_tenant(tenant_id):
    try:
        claims = get_jwt()
        if claims.get("role") == "store_admin" and claims.get("tenant_id") != tenant_id:
            return jsonify({"success": False, "message": "Access denied"}), 403

        data = request.get_json()
        result = TenantService.update_tenant(tenant_id, data or {})
        status = 200 if result["success"] else 400
        return jsonify(result), status

    except Exception as e:
        logger.error(f"Error updating tenant: {e}")
        return jsonify({"success": False, "message": "Failed to update store"}), 500


@tenant_bp.route("/<tenant_id>/stats", methods=["GET"])
@store_admin_required
def get_tenant_stats(tenant_id):
    try:
        claims = get_jwt()
        if claims.get("role") == "store_admin" and claims.get("tenant_id") != tenant_id:
            return jsonify({"success": False, "message": "Access denied"}), 403

        stats = TenantService.get_tenant_stats(tenant_id)
        return jsonify({"success": True, "stats": stats}), 200

    except Exception as e:
        logger.error(f"Error getting tenant stats: {e}")
        return jsonify({"success": False, "message": "Failed to get stats"}), 500
