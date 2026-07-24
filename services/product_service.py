import logging
import uuid
from typing import Any, Dict, List, Optional

from models.product import Product
from models.tenant import Tenant
from utils.tenant_context import get_current_tenant_id

from .vector_service import VectorService

logger = logging.getLogger(__name__)


class ProductService:
    """Service for product-related operations."""

    def __init__(self):
        self.vector_service = VectorService()

    def _get_namespace(self, tenant_id: str) -> str:
        tenant = Tenant.query.get(tenant_id)
        return tenant.slug if tenant else ""

    def _build_metadata(self, product: Product) -> Dict[str, Any]:
        return {
            "tenant_id": product.tenant_id,
            "category": product.category,
            "subcategory": product.subcategory,
            "brand": product.brand,
            "price": product.price,
            "rating": product.rating,
            "in_stock": product.is_in_stock(),
        }

    def create_product(
        self, product_data: Dict[str, Any], tenant_id: str = None
    ) -> Product:
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            if not resolved_tenant_id:
                raise ValueError("Tenant context is required")

            if "id" not in product_data:
                product_data["id"] = str(uuid.uuid4())
            product_data["tenant_id"] = resolved_tenant_id

            product = Product(**product_data)

            from app import db

            db.session.add(product)
            db.session.flush()

            namespace = self._get_namespace(resolved_tenant_id)
            self.vector_service.upsert_product_embedding(
                product.id,
                product.get_search_text(),
                self._build_metadata(product),
                namespace=namespace,
            )

            product.embedding_id = product.id
            db.session.commit()

            logger.info(f"Created product: {product.name} (tenant: {resolved_tenant_id})")
            return product

        except Exception as e:
            logger.error(f"Error creating product: {str(e)}")
            from app import db

            db.session.rollback()
            raise

    def update_product(
        self,
        product_id: str,
        update_data: Dict[str, Any],
        tenant_id: str = None,
    ) -> Optional[Product]:
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            query = Product.query.filter_by(id=product_id)
            if resolved_tenant_id:
                query = query.filter_by(tenant_id=resolved_tenant_id)
            product = query.first()
            if not product:
                return None

            for key, value in update_data.items():
                if hasattr(product, key) and key not in ("id", "tenant_id"):
                    setattr(product, key, value)

            content_fields = [
                "name",
                "description",
                "features",
                "category",
                "subcategory",
                "brand",
            ]
            if any(field in update_data for field in content_fields):
                namespace = self._get_namespace(product.tenant_id)
                self.vector_service.upsert_product_embedding(
                    product.id,
                    product.get_search_text(),
                    self._build_metadata(product),
                    namespace=namespace,
                )

            from app import db

            db.session.commit()
            return product

        except Exception as e:
            logger.error(f"Error updating product: {str(e)}")
            from app import db

            db.session.rollback()
            raise

    def delete_product(self, product_id: str, tenant_id: str = None) -> bool:
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            query = Product.query.filter_by(id=product_id)
            if resolved_tenant_id:
                query = query.filter_by(tenant_id=resolved_tenant_id)
            product = query.first()
            if not product:
                return False

            namespace = self._get_namespace(product.tenant_id)
            self.vector_service.delete_product_embedding(product.id, namespace=namespace)

            from app import db

            db.session.delete(product)
            db.session.commit()
            return True

        except Exception as e:
            logger.error(f"Error deleting product: {str(e)}")
            from app import db

            db.session.rollback()
            raise

    def search_products(
        self,
        query: str,
        filters: Dict[str, Any] = None,
        limit: int = 20,
        tenant_id: str = None,
    ) -> List[Product]:
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            namespace = self._get_namespace(resolved_tenant_id) if resolved_tenant_id else ""

            vector_results = self.vector_service.search_similar_products(
                query,
                top_k=limit * 2,
                namespace=namespace,
            )

            if not vector_results:
                return Product.search_by_filters(
                    tenant_id=resolved_tenant_id,
                    search_query=query,
                    limit=limit,
                    **(filters or {}),
                )

            product_ids = [result["id"] for result in vector_results]
            query_builder = Product.query.filter(Product.id.in_(product_ids))

            if resolved_tenant_id:
                query_builder = query_builder.filter(
                    Product.tenant_id == resolved_tenant_id
                )

            if filters:
                if filters.get("category"):
                    query_builder = query_builder.filter(
                        Product.category == filters["category"]
                    )
                if filters.get("subcategory"):
                    query_builder = query_builder.filter(
                        Product.subcategory == filters["subcategory"]
                    )
                if filters.get("brand"):
                    query_builder = query_builder.filter(
                        Product.brand == filters["brand"]
                    )
                if filters.get("min_price") is not None:
                    query_builder = query_builder.filter(
                        Product.price >= filters["min_price"]
                    )
                if filters.get("max_price") is not None:
                    query_builder = query_builder.filter(
                        Product.price <= filters["max_price"]
                    )
                if filters.get("min_rating") is not None:
                    query_builder = query_builder.filter(
                        Product.rating >= filters["min_rating"]
                    )
                if filters.get("in_stock_only"):
                    query_builder = query_builder.filter(Product.stock > 0)

            products = query_builder.all()
            product_score_map = {
                result["id"]: result["score"] for result in vector_results
            }
            products.sort(key=lambda p: product_score_map.get(p.id, 0), reverse=True)
            return products[:limit]

        except Exception as e:
            logger.error(f"Error searching products: {str(e)}")
            return []

    def get_recommendations(
        self,
        product_id: str = None,
        user_preferences: Dict[str, Any] = None,
        limit: int = 6,
        tenant_id: str = None,
    ) -> List[Product]:
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            namespace = self._get_namespace(resolved_tenant_id) if resolved_tenant_id else ""

            if product_id:
                query = Product.query.filter_by(id=product_id)
                if resolved_tenant_id:
                    query = query.filter_by(tenant_id=resolved_tenant_id)
                product = query.first()
                if not product:
                    return []

                similar_results = self.vector_service.search_similar_products(
                    product.get_search_text(),
                    top_k=limit + 1,
                    namespace=namespace,
                )
                similar_ids = [
                    r["id"] for r in similar_results if r["id"] != product_id
                ]

            elif user_preferences:
                pref_text = self._build_preference_text(user_preferences)
                similar_results = self.vector_service.search_similar_products(
                    pref_text, top_k=limit, namespace=namespace
                )
                similar_ids = [r["id"] for r in similar_results]

            else:
                query = Product.query.filter(Product.is_active == True)
                if resolved_tenant_id:
                    query = query.filter(Product.tenant_id == resolved_tenant_id)
                return query.order_by(Product.rating.desc()).limit(limit).all()

            products_query = Product.query.filter(Product.id.in_(similar_ids))
            if resolved_tenant_id:
                products_query = products_query.filter(
                    Product.tenant_id == resolved_tenant_id
                )
            products = products_query.all()

            if similar_results:
                score_map = {r["id"]: r["score"] for r in similar_results}
                products.sort(key=lambda p: score_map.get(p.id, 0), reverse=True)

            return products[:limit]

        except Exception as e:
            logger.error(f"Error getting recommendations: {str(e)}")
            return []

    def _build_preference_text(self, preferences: Dict[str, Any]) -> str:
        text_parts = []
        if preferences.get("favoriteCategories"):
            text_parts.extend(preferences["favoriteCategories"])
        if preferences.get("favoriteBrands"):
            text_parts.extend(preferences["favoriteBrands"])
        if preferences.get("priceRange"):
            min_price, max_price = preferences["priceRange"]
            if max_price < 500000:
                text_parts.append("giá rẻ tiết kiệm")
            elif max_price > 1500000:
                text_parts.append("cao cấp premium")
            else:
                text_parts.append("tầm trung")
        return " ".join(text_parts) if text_parts else "sơn nội thất ngoại thất phổ biến"

    def bulk_generate_embeddings(self, tenant_id: str = None):
        try:
            resolved_tenant_id = tenant_id or get_current_tenant_id()
            query = Product.query.filter(Product.is_active == True)
            if resolved_tenant_id:
                query = query.filter(Product.tenant_id == resolved_tenant_id)
            products = query.all()

            namespace = self._get_namespace(resolved_tenant_id) if resolved_tenant_id else ""
            batch_data = []
            for product in products:
                batch_data.append(
                    {
                        "id": product.id,
                        "text": product.get_search_text(),
                        "metadata": self._build_metadata(product),
                    }
                )

            self.vector_service.batch_upsert_products(batch_data, namespace=namespace)

            for product in products:
                product.embedding_id = product.id

            from app import db

            db.session.commit()
            return len(products)

        except Exception as e:
            logger.error(f"Error generating bulk embeddings: {str(e)}")
            raise
