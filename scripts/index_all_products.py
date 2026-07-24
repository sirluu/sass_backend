from app import create_app
from extensions import db
from models import Product, Tenant
from services.vector_service import VectorService


def main():
    app = create_app()
    with app.app_context():
        vector_service = VectorService()
        vector_service.initialize()

        tenants = Tenant.query.filter_by(is_active=True).all()
        if not tenants:
            print("No active tenants found.")
            return

        total_indexed = 0
        for tenant in tenants:
            products = Product.query.filter_by(
                tenant_id=tenant.id, is_active=True
            ).all()
            print(f"Tenant {tenant.slug}: {len(products)} products to index...")

            product_dicts = []
            for product in products:
                product_dicts.append(
                    {
                        "id": product.id,
                        "text": product.get_search_text(),
                        "metadata": {
                            "tenant_id": product.tenant_id,
                            "category": product.category,
                            "subcategory": product.subcategory,
                            "brand": product.brand,
                            "price": product.price,
                            "rating": product.rating,
                            "in_stock": product.is_in_stock(),
                        },
                    }
                )

            if product_dicts:
                vector_service.batch_upsert_products(product_dicts, namespace=tenant.slug)
                total_indexed += len(product_dicts)

        print(f"Indexed {total_indexed} products across {len(tenants)} tenants.")


if __name__ == "__main__":
    main()
