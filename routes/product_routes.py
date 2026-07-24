from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity, verify_jwt_in_request
import logging
from sqlalchemy import distinct, func

from models.product import Product
from services.product_service import ProductService
from services.auth_service import AuthService
from utils.decorators import store_admin_required, tenant_required
from utils.tenant_context import get_current_tenant_id

logger = logging.getLogger(__name__)
product_bp = Blueprint("products", __name__)
product_service = ProductService()


@product_bp.route("/", methods=["GET"])
@tenant_required
def get_products():
    try:
        tenant_id = get_current_tenant_id()
        category = request.args.get("category")
        subcategory = request.args.get("subcategory")
        brand = request.args.get("brand")
        min_price = request.args.get("min_price", type=float)
        max_price = request.args.get("max_price", type=float)
        min_rating = request.args.get("min_rating", type=float)
        in_stock_only = request.args.get("in_stock_only", "false").lower() == "true"
        search_query = request.args.get("search")
        limit = request.args.get("limit", 50, type=int)

        if not search_query:
            products = Product.search_by_filters(
                tenant_id=tenant_id,
                category=category,
                subcategory=subcategory,
                brand=brand,
                min_price=min_price,
                max_price=max_price,
                min_rating=min_rating,
                in_stock_only=in_stock_only,
                limit=limit,
            )
        else:
            filters = {}
            if category:
                filters["category"] = category
            if subcategory:
                filters["subcategory"] = subcategory
            if brand:
                filters["brand"] = brand
            if min_price is not None:
                filters["min_price"] = min_price
            if max_price is not None:
                filters["max_price"] = max_price
            if min_rating is not None:
                filters["min_rating"] = min_rating
            if in_stock_only:
                filters["in_stock_only"] = in_stock_only

            products = product_service.search_products(
                search_query, filters, limit, tenant_id=tenant_id
            )

        return jsonify(
            {
                "success": True,
                "products": [product.to_dict() for product in products],
                "count": len(products),
            }
        ), 200

    except Exception as e:
        logger.error(f"Error in get_products endpoint: {str(e)}")
        return jsonify({"success": False, "message": "Failed to get products"}), 500


@product_bp.route("/<product_id>", methods=["GET"])
@tenant_required
def get_product(product_id):
    try:
        tenant_id = get_current_tenant_id()
        product = Product.query.filter_by(
            id=product_id, tenant_id=tenant_id
        ).first()

        if not product:
            return jsonify({"success": False, "message": "Product not found"}), 404

        return jsonify({"success": True, "product": product.to_dict()}), 200

    except Exception as e:
        logger.error(f"Error in get_product endpoint: {str(e)}")
        return jsonify({"success": False, "message": "Failed to get product"}), 500


@product_bp.route("/search", methods=["POST"])
@tenant_required
def search_products():
    try:
        tenant_id = get_current_tenant_id()
        data = request.get_json()

        if not data or not data.get("query"):
            return jsonify(
                {"success": False, "message": "Search query is required"}
            ), 400

        products = product_service.search_products(
            data["query"],
            data.get("filters", {}),
            data.get("limit", 20),
            tenant_id=tenant_id,
        )

        return jsonify(
            {
                "success": True,
                "products": [product.to_dict() for product in products],
                "count": len(products),
                "query": data["query"],
            }
        ), 200

    except Exception as e:
        logger.error(f"Error in search_products endpoint: {str(e)}")
        return jsonify({"success": False, "message": "Search failed"}), 500


@product_bp.route("/recommendations", methods=["GET"])
@tenant_required
def get_recommendations():
    try:
        tenant_id = get_current_tenant_id()
        product_id = request.args.get("product_id")
        limit = request.args.get("limit", 6, type=int)

        user_preferences = None
        try:
            verify_jwt_in_request(optional=True)
            current_user_id = get_jwt_identity()
            if current_user_id:
                user = AuthService.get_user_by_id(current_user_id)
                if user:
                    user_preferences = user.get_preferences()
        except Exception:
            pass

        recommendations = product_service.get_recommendations(
            product_id=product_id,
            user_preferences=user_preferences,
            limit=limit,
            tenant_id=tenant_id,
        )

        return jsonify(
            {
                "success": True,
                "recommendations": [product.to_dict() for product in recommendations],
                "count": len(recommendations),
            }
        ), 200

    except Exception as e:
        logger.error(f"Error in get_recommendations endpoint: {str(e)}")
        return jsonify(
            {"success": False, "message": "Failed to get recommendations"}
        ), 500


@product_bp.route("/categories", methods=["GET"])
@tenant_required
def get_categories():
    try:
        tenant_id = get_current_tenant_id()
        categories = (
            Product.query.with_entities(distinct(Product.category), Product.subcategory)
            .filter(Product.is_active == True, Product.tenant_id == tenant_id)
            .all()
        )

        category_map = {}
        for category, subcategory in categories:
            if category not in category_map:
                category_map[category] = set()
            category_map[category].add(subcategory)

        result = [
            {"category": category, "subcategories": list(subcategories)}
            for category, subcategories in category_map.items()
        ]

        return jsonify({"success": True, "categories": result}), 200

    except Exception as e:
        logger.error(f"Error in get_categories endpoint: {str(e)}")
        return jsonify({"success": False, "message": "Failed to get categories"}), 500


@product_bp.route("/brands", methods=["GET"])
@tenant_required
def get_brands():
    try:
        tenant_id = get_current_tenant_id()
        brands = (
            Product.query.with_entities(distinct(Product.brand))
            .filter(Product.is_active == True, Product.tenant_id == tenant_id)
            .order_by(Product.brand)
            .all()
        )

        return jsonify({"success": True, "brands": [brand[0] for brand in brands]}), 200

    except Exception as e:
        logger.error(f"Error in get_brands endpoint: {str(e)}")
        return jsonify({"success": False, "message": "Failed to get brands"}), 500


@product_bp.route("/stats", methods=["GET"])
@tenant_required
def get_product_stats():
    try:
        tenant_id = get_current_tenant_id()
        base_query = Product.query.filter(
            Product.is_active == True, Product.tenant_id == tenant_id
        )

        stats = {
            "total_products": base_query.count(),
            "total_categories": Product.query.with_entities(
                func.count(func.distinct(Product.category))
            )
            .filter(Product.is_active == True, Product.tenant_id == tenant_id)
            .scalar(),
            "total_brands": Product.query.with_entities(
                func.count(func.distinct(Product.brand))
            )
            .filter(Product.is_active == True, Product.tenant_id == tenant_id)
            .scalar(),
            "price_range": {
                "min": Product.query.with_entities(func.min(Product.price))
                .filter(Product.tenant_id == tenant_id)
                .scalar()
                or 0,
                "max": Product.query.with_entities(func.max(Product.price))
                .filter(Product.tenant_id == tenant_id)
                .scalar()
                or 0,
            },
            "average_rating": round(
                Product.query.with_entities(func.avg(Product.rating))
                .filter(Product.tenant_id == tenant_id)
                .scalar()
                or 0,
                2,
            ),
            "in_stock_count": base_query.filter(Product.stock > 0).count(),
        }

        return jsonify({"success": True, "stats": stats}), 200

    except Exception as e:
        logger.error(f"Error in get_product_stats endpoint: {str(e)}")
        return jsonify(
            {"success": False, "message": "Failed to get product statistics"}
        ), 500


@product_bp.route("/", methods=["POST"])
@store_admin_required
def create_product():
    try:
        data = request.get_json()
        required_fields = [
            "name",
            "description",
            "price",
            "category",
            "subcategory",
            "brand",
        ]
        if not all(field in data for field in required_fields):
            return jsonify(
                {"success": False, "message": "Missing required fields"}
            ), 400

        product = product_service.create_product(data)
        return jsonify(
            {
                "success": True,
                "message": "Product created successfully",
                "product": product.to_dict(),
            }
        ), 201

    except Exception as e:
        logger.error(f"Error in create_product endpoint: {str(e)}")
        return jsonify({"success": False, "message": "Failed to create product"}), 500


@product_bp.route("/<product_id>", methods=["PUT"])
@store_admin_required
def update_product(product_id):
    try:
        data = request.get_json()
        if not data:
            return jsonify(
                {"success": False, "message": "Update data is required"}
            ), 400

        product = product_service.update_product(product_id, data)
        if not product:
            return jsonify({"success": False, "message": "Product not found"}), 404

        return jsonify(
            {
                "success": True,
                "message": "Product updated successfully",
                "product": product.to_dict(),
            }
        ), 200

    except Exception as e:
        logger.error(f"Error in update_product endpoint: {str(e)}")
        return jsonify({"success": False, "message": "Failed to update product"}), 500


@product_bp.route("/<product_id>", methods=["DELETE"])
@store_admin_required
def delete_product(product_id):
    try:
        success = product_service.delete_product(product_id)
        if not success:
            return jsonify({"success": False, "message": "Product not found"}), 404

        return jsonify(
            {"success": True, "message": "Product deleted successfully"}
        ), 200

    except Exception as e:
        logger.error(f"Error in delete_product endpoint: {str(e)}")
        return jsonify({"success": False, "message": "Failed to delete product"}), 500
