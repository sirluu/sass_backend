import logging
import uuid

from models.product import Product
from models.tenant import Tenant
from models.user import User
from services.product_service import ProductService

logger = logging.getLogger(__name__)


class DatabaseSeeder:
    """Utility class for seeding multi-tenant paint store data."""

    DEFAULT_TENANTS = [
        {
            "id": "tenant-son-phong-thuy",
            "name": "Sơn Phong Thủy",
            "slug": "son-phong-thuy",
            "domain": None,
            "description": "Cửa hàng sơn phong thủy - tư vấn màu sắc theo phong thủy",
        },
        {
            "id": "tenant-son-jotun-hanoi",
            "name": "Sơn Jotun Hà Nội",
            "slug": "son-jotun-hanoi",
            "domain": None,
            "description": "Đại lý chính thức Jotun tại Hà Nội",
        },
        {
            "id": "tenant-son-dulux-danang",
            "name": "Sơn Dulux Đà Nẵng",
            "slug": "son-dulux-danang",
            "domain": None,
            "description": "Cửa hàng sơn Dulux uy tín tại Đà Nẵng",
        },
    ]

    def __init__(self, db):
        self.db = db
        self.product_service = ProductService()

    def seed_all(self, force=False):
        self.seed_tenants(force=force)
        self.seed_users(force=force)
        self.seed_products(force=force)

    def seed_tenants(self, force=False):
        try:
            if Tenant.query.count() > 0 and not force:
                logger.info("Tenants already exist, skipping tenant seeding")
                return

            if force:
                Tenant.query.delete()
                self.db.session.commit()

            for tenant_data in self.DEFAULT_TENANTS:
                tenant = Tenant(
                    id=tenant_data["id"],
                    name=tenant_data["name"],
                    slug=tenant_data["slug"],
                    domain=tenant_data.get("domain"),
                    description=tenant_data.get("description"),
                    is_active=True,
                )
                tenant.set_settings(
                    {
                        "primaryColor": "#2563eb",
                        "currency": "VND",
                        "chatbotName": "Tư vấn sơn AI",
                    }
                )
                self.db.session.add(tenant)

            self.db.session.commit()
            logger.info(f"Seeded {len(self.DEFAULT_TENANTS)} paint stores")

        except Exception as e:
            logger.error(f"Error seeding tenants: {e}")
            self.db.session.rollback()
            raise

    def seed_users(self, force=False):
        try:
            if User.query.filter_by(role="super_admin").first() and not force:
                logger.info("Admin users already exist, skipping user seeding")
                return

            super_admin = User(
                id="user-super-admin",
                email="admin@platform.com",
                name="Platform Admin",
                password="admin123",
                tenant_id=None,
                role="super_admin",
            )
            self.db.session.add(super_admin)

            store_admins = [
                ("tenant-son-phong-thuy", "admin@sonphongthuy.com", "Admin Phong Thủy"),
                ("tenant-son-jotun-hanoi", "admin@jotunhanoi.com", "Admin Jotun HN"),
                ("tenant-son-dulux-danang", "admin@duluxdanang.com", "Admin Dulux DN"),
            ]
            for tenant_id, email, name in store_admins:
                if not User.query.filter_by(email=email, tenant_id=tenant_id).first():
                    self.db.session.add(
                        User(
                            id=str(uuid.uuid4()),
                            email=email,
                            name=name,
                            password="admin123",
                            tenant_id=tenant_id,
                            role="store_admin",
                        )
                    )

            self.db.session.commit()
            logger.info("Seeded super admin and store admins (password: admin123)")

        except Exception as e:
            logger.error(f"Error seeding users: {e}")
            self.db.session.rollback()
            raise

    def seed_products(self, force=False):
        """Seed paint products for each tenant store."""
        try:
            tenants = Tenant.query.filter_by(is_active=True).all()
            if not tenants:
                logger.warning("No tenants found, skipping product seeding")
                return

            if Product.query.count() > 0 and not force:
                logger.info("Products already exist, skipping product seeding")
                return

            if force and Product.query.count() > 0:
                self._clear_products()

            products_data = self._get_sample_products()
            logger.info(
                f"Seeding {len(products_data)} products per store for {len(tenants)} stores..."
            )

            for tenant in tenants:
                namespace = tenant.slug
                for product_data in products_data:
                    try:
                        item = dict(product_data)
                        item["id"] = str(uuid.uuid4())
                        item["tenant_id"] = tenant.id
                        product = Product(**item)
                        self.db.session.add(product)
                        self.db.session.flush()

                        metadata = {
                            "tenant_id": tenant.id,
                            "category": product.category,
                            "subcategory": product.subcategory,
                            "brand": product.brand,
                            "price": product.price,
                            "rating": product.rating,
                            "in_stock": product.is_in_stock(),
                        }

                        self.product_service.vector_service.upsert_product_embedding(
                            product.id,
                            product.get_search_text(),
                            metadata,
                            namespace=namespace,
                        )
                        product.embedding_id = product.id

                    except Exception as e:
                        logger.error(
                            f"Error seeding product for {tenant.slug}: {e}"
                        )
                        continue

            self.db.session.commit()
            logger.info("Multi-tenant paint products seeded successfully")

        except Exception as e:
            logger.error(f"Error seeding products: {e}")
            self.db.session.rollback()
            raise

    def _clear_products(self):
        """Remove all products from DB and Pinecone."""
        products = Product.query.all()
        for product in products:
            try:
                namespace = product.tenant.slug if product.tenant else ""
                self.product_service.vector_service.delete_product_embedding(
                    product.id, namespace=namespace
                )
            except Exception as e:
                logger.warning(f"Could not delete embedding for {product.id}: {e}")

        Product.query.delete()
        self.db.session.commit()
        logger.info(f"Cleared {len(products)} existing products")

    def _get_sample_products(self):
        """Sample paint products for interior/exterior retail."""
        return [
            # ── Sơn nội thất ──────────────────────────────────────────────
            {
                "id": str(uuid.uuid4()),
                "name": "Dulux Easy Clean Matt - Trắng Tuyết",
                "description": (
                    "Sơn nội thất cao cấp dòng Easy Clean, hoàn thiện mờ (matt), "
                    "dễ lau chùi vết bẩn trên tường phòng khách và phòng ăn. "
                    "Không mùi, an toàn cho gia đình có trẻ em. "
                    "Định mức phủ 10-12 m²/lít/lớp trên bề mặt đã bả."
                ),
                "price": 890000,
                "original_price": 990000,
                "category": "Sơn nội thất",
                "subcategory": "Sơn mờ nội thất",
                "brand": "Dulux",
                "rating": 4.8,
                "review_count": 1240,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 85,
                "features": [
                    "Hoàn thiện mờ (Matt)",
                    "Dễ lau chùi",
                    "Không mùi",
                    "Quy cách 5L",
                    "Phù hợp phòng khách, phòng ăn",
                    "Định mức 10-12 m²/lít/lớp",
                ],
                "is_on_sale": True,
                "sale_percentage": 10,
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Dulux Ambiance Diamond Matt - Xám Bạc",
                "description": (
                    "Sơn nội thất cao cấp nhất dòng Ambiance với công nghệ Diamond Matt, "
                    "bề mặt mịn như lụa, chống bám bụi. Lý tưởng cho phòng ngủ master "
                    "và không gian sang trọng. Màu xám bạc hiện đại, phong cách Bắc Âu."
                ),
                "price": 1450000,
                "category": "Sơn nội thất",
                "subcategory": "Sơn mờ cao cấp",
                "brand": "Dulux",
                "rating": 4.9,
                "review_count": 876,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 42,
                "features": [
                    "Công nghệ Diamond Matt",
                    "Bề mặt mịn như lụa",
                    "Chống bám bụi",
                    "Quy cách 5L",
                    "Phù hợp phòng ngủ cao cấp",
                    "Màu xám Bắc Âu",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Jotun Jotashield Colour - Kem Ấm",
                "description": (
                    "Sơn nội thất Jotashield Colour với bảng màu phong phú, "
                    "độ che phủ cao, bền màu lâu. Phù hợp phòng ngủ, phòng làm việc. "
                    "Màu kem ấm tạo cảm giác ấm cúng, thư giãn."
                ),
                "price": 720000,
                "category": "Sơn nội thất",
                "subcategory": "Sơn mờ nội thất",
                "brand": "Jotun",
                "rating": 4.7,
                "review_count": 2103,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 120,
                "features": [
                    "Độ che phủ cao",
                    "Bền màu",
                    "Quy cách 5L",
                    "Phù hợp phòng ngủ",
                    "Màu kem ấm",
                    "Định mức 12-14 m²/lít/lớp",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Jotun Lady Balance - Xanh Mint Nhẹ",
                "description": (
                    "Sơn nội thất Jotun Lady Balance không chứa APEO, an toàn cho phòng em bé "
                    "và phụ nữ mang thai. Khử mùi, chống nấm mốc trong nhà. "
                    "Màu xanh mint nhẹ nhàng, tươi mát cho phòng trẻ em."
                ),
                "price": 980000,
                "category": "Sơn nội thất",
                "subcategory": "Sơn phòng em bé",
                "brand": "Jotun",
                "rating": 4.9,
                "review_count": 654,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 56,
                "features": [
                    "Không APEO",
                    "An toàn phòng em bé",
                    "Khử mùi",
                    "Chống nấm mốc trong nhà",
                    "Quy cách 5L",
                    "Màu xanh mint",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Nippon Paint Odour-less AirCare - Trắng Sứ",
                "description": (
                    "Sơn nội thất Nippon Odour-less AirCare lọc không khí, "
                    "giảm formaldehyde, phù hợp phòng ngủ và phòng khách. "
                    "Không mùi ngay khi sơn, thân thiện môi trường."
                ),
                "price": 850000,
                "original_price": 920000,
                "category": "Sơn nội thất",
                "subcategory": "Sơn không mùi",
                "brand": "Nippon Paint",
                "rating": 4.6,
                "review_count": 1890,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 95,
                "features": [
                    "Lọc không khí",
                    "Giảm formaldehyde",
                    "Không mùi",
                    "Quy cách 5L",
                    "Phù hợp phòng ngủ",
                    "Định mức 11-13 m²/lít/lớp",
                ],
                "is_on_sale": True,
                "sale_percentage": 8,
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Kova Semi-Gloss - Trắng Nhà Vệ Sinh",
                "description": (
                    "Sơn nội thất Kova bán bóng (semi-gloss), chống ẩm mốc, "
                    "chống thấm nước, dễ lau chùi. Chuyên dụng cho nhà vệ sinh, "
                    "phòng tắm, bếp và khu vực ẩm ướt."
                ),
                "price": 380000,
                "category": "Sơn nội thất",
                "subcategory": "Sơn bán bóng chống ẩm",
                "brand": "Kova",
                "rating": 4.5,
                "review_count": 3421,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 200,
                "features": [
                    "Hoàn thiện bán bóng",
                    "Chống ẩm mốc",
                    "Chống thấm nước",
                    "Dễ lau chùi",
                    "Quy cách 5L",
                    "Phù hợp nhà vệ sinh, phòng tắm",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "MyKolor Charming Matt - Hồng Pastel",
                "description": (
                    "Sơn nội thất MyKolor Charming Matt giá tốt, màu pastel dễ thương "
                    "cho phòng ngủ bé gái và không gian trang trí. "
                    "Độ che phủ tốt trên tường đã bả mịn."
                ),
                "price": 420000,
                "category": "Sơn nội thất",
                "subcategory": "Sơn mờ giá rẻ",
                "brand": "MyKolor",
                "rating": 4.3,
                "review_count": 987,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 150,
                "features": [
                    "Giá tốt",
                    "Màu pastel",
                    "Hoàn thiện mờ",
                    "Quy cách 5L",
                    "Phù hợp phòng trẻ em",
                    "Định mức 10 m²/lít/lớp",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Dulux Wash & Wear Gloss - Trắng Bóng",
                "description": (
                    "Sơn nội thất bóng (gloss) dòng Wash & Wear, bề mặt sáng bóng, "
                    "dễ vệ sinh, chống lem vết. Phù hợp phòng bếp, hành lang và "
                    "khu vực cần thường xuyên lau chùi."
                ),
                "price": 950000,
                "category": "Sơn nội thất",
                "subcategory": "Sơn bóng nội thất",
                "brand": "Dulux",
                "rating": 4.6,
                "review_count": 756,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 68,
                "features": [
                    "Hoàn thiện bóng (Gloss)",
                    "Dễ vệ sinh",
                    "Chống lem vết",
                    "Quy cách 5L",
                    "Phù hợp phòng bếp, hành lang",
                ],
            },
            # ── Sơn ngoại thất ────────────────────────────────────────────
            {
                "id": str(uuid.uuid4()),
                "name": "Dulux Weathershield Powerflexx - Trắng Ngà",
                "description": (
                    "Sơn ngoại thất cao cấp Dulux Weathershield Powerflexx chống nứt, "
                    "chống thấm, chịu thời tiết khắc nghiệt. Công nghệ Powerflexx co giãn "
                    "theo nhiệt độ, bảo vệ mặt tiền nhà và tường ngoài trời lâu dài."
                ),
                "price": 1280000,
                "original_price": 1380000,
                "category": "Sơn ngoại thất",
                "subcategory": "Sơn chống thời tiết",
                "brand": "Dulux",
                "rating": 4.9,
                "review_count": 1567,
                "image_url": "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
                "stock": 74,
                "features": [
                    "Chống nứt Powerflexx",
                    "Chống thấm",
                    "Chịu thời tiết",
                    "Quy cách 5L",
                    "Phù hợp mặt tiền, tường ngoài",
                    "Định mức 8-10 m²/lít/lớp",
                ],
                "is_on_sale": True,
                "sale_percentage": 7,
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Jotun Jotashield Extreme - Xám Đá",
                "description": (
                    "Sơn ngoại thất Jotun Jotashield Extreme chống phai màu, "
                    "chống nấm mốc, chống kiềm. Bảo vệ tường ngoài trời 10+ năm. "
                    "Màu xám đá sang trọng cho biệt thự và nhà phố hiện đại."
                ),
                "price": 1150000,
                "category": "Sơn ngoại thất",
                "subcategory": "Sơn chống thời tiết",
                "brand": "Jotun",
                "rating": 4.8,
                "review_count": 2340,
                "image_url": "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
                "stock": 88,
                "features": [
                    "Chống phai màu 10+ năm",
                    "Chống nấm mốc",
                    "Chống kiềm",
                    "Quy cách 5L",
                    "Phù hợp biệt thự, nhà phố",
                    "Màu xám đá",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Kova Son Ngoai That - Vàng Kem",
                "description": (
                    "Sơn ngoại thất Kova giá hợp lý, chống thấm, chống muối hóa. "
                    "Phù hợp nhà ở dân dụng, công trình sửa chữa. "
                    "Màu vàng kem ấm áp cho mặt tiền nhà phố."
                ),
                "price": 520000,
                "category": "Sơn ngoại thất",
                "subcategory": "Sơn ngoại thất giá rẻ",
                "brand": "Kova",
                "rating": 4.4,
                "review_count": 4567,
                "image_url": "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
                "stock": 180,
                "features": [
                    "Giá hợp lý",
                    "Chống thấm",
                    "Chống muối hóa",
                    "Quy cách 5L",
                    "Phù hợp nhà dân dụng",
                    "Định mức 8 m²/lít/lớp",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Nippon Paint Weatherbond - Xanh Lá Nhạt",
                "description": (
                    "Sơn ngoại thất Nippon Weatherbond chống tia UV, chống phai màu, "
                    "thoát hơi ẩm tốt. Phù hợp vùng nhiệt đới nóng ẩm như Việt Nam. "
                    "Màu xanh lá nhạt tươi mát cho sân vườn và ban công."
                ),
                "price": 780000,
                "category": "Sơn ngoại thất",
                "subcategory": "Sơn chống UV",
                "brand": "Nippon Paint",
                "rating": 4.6,
                "review_count": 1234,
                "image_url": "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
                "stock": 92,
                "features": [
                    "Chống tia UV",
                    "Chống phai màu",
                    "Thoát hơi ẩm",
                    "Quy cách 5L",
                    "Phù hợp vùng nhiệt đới",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Dulux Weathershield Anti-Mould - Trắng",
                "description": (
                    "Sơn ngoại thất chuyên chống nấm mốc Dulux Weathershield Anti-Mould. "
                    "Ngăn nấm mốc phát triển trên tường ngoài trời, đặc biệt khu vực "
                    "ẩm ướt, gần sông hồ, tầng hầm ngoài trời."
                ),
                "price": 1350000,
                "category": "Sơn ngoại thất",
                "subcategory": "Sơn chống nấm mốc",
                "brand": "Dulux",
                "rating": 4.7,
                "review_count": 890,
                "image_url": "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
                "stock": 55,
                "features": [
                    "Chống nấm mốc chuyên dụng",
                    "Chống thấm",
                    "Chịu thời tiết",
                    "Quy cách 5L",
                    "Phù hợp khu vực ẩm ướt",
                ],
            },
            # ── Sơn lót ───────────────────────────────────────────────────
            {
                "id": str(uuid.uuid4()),
                "name": "Dulux Sealer Wall Sealer - Sơn Lót Nội Thất",
                "description": (
                    "Sơn lót nội thất Dulux Wall Sealer thấm sâu, tăng độ bám dính, "
                    "giảm lượng sơn phủ. Bắt buộc sử dụng trước khi sơn phủ nội thất "
                    "trên tường mới hoặc tường đã bả."
                ),
                "price": 450000,
                "category": "Sơn lót",
                "subcategory": "Sơn lót nội thất",
                "brand": "Dulux",
                "rating": 4.7,
                "review_count": 2100,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 160,
                "features": [
                    "Thấm sâu",
                    "Tăng độ bám dính",
                    "Giảm lượng sơn phủ",
                    "Quy cách 5L",
                    "Dùng trước sơn phủ nội thất",
                    "Định mức 12-15 m²/lít/lớp",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Jotun Jotashield Primer - Sơn Lót Ngoại Thất",
                "description": (
                    "Sơn lót ngoại thất Jotun Jotashield Primer chống kiềm, "
                    "chống muối hóa, tăng độ bám dính sơn phủ ngoại thất. "
                    "Dùng trên tường gạch, bê tông, tường cũ trước khi sơn phủ."
                ),
                "price": 580000,
                "category": "Sơn lót",
                "subcategory": "Sơn lót ngoại thất",
                "brand": "Jotun",
                "rating": 4.8,
                "review_count": 1456,
                "image_url": "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
                "stock": 110,
                "features": [
                    "Chống kiềm",
                    "Chống muối hóa",
                    "Tăng bám dính",
                    "Quy cách 5L",
                    "Dùng trước sơn phủ ngoại thất",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Kova Son Lot Chong Kiem - Sơn Lót Chống Kiềm",
                "description": (
                    "Sơn lót chống kiềm Kova ngăn kiềm từ tường gạch, bê tông thấm ra "
                    "làm bong tróc sơn phủ. Giá tốt, phù hợp công trình dân dụng "
                    "và sửa chữa nhà cũ."
                ),
                "price": 320000,
                "category": "Sơn lót",
                "subcategory": "Sơn lót chống kiềm",
                "brand": "Kova",
                "rating": 4.5,
                "review_count": 3210,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 220,
                "features": [
                    "Chống kiềm",
                    "Ngăn bong tróc",
                    "Giá tốt",
                    "Quy cách 5L",
                    "Phù hợp tường gạch, bê tông",
                ],
            },
            # ── Sơn chống thấm ─────────────────────────────────────────────
            {
                "id": str(uuid.uuid4()),
                "name": "Sika SikaTop Seal-107 - Sơn Chống Thấm Mái",
                "description": (
                    "Sơn chống thấm gốc xi măng Sika SikaTop Seal-107 cho mái nhà, "
                    "sân thượng, ban công. Chống thấm 2 chiều, chống nứt, "
                    "chịu nước đứng và áp lực nước nhẹ."
                ),
                "price": 1650000,
                "category": "Sơn chống thấm",
                "subcategory": "Chống thấm mái sân thượng",
                "brand": "Sika",
                "rating": 4.8,
                "review_count": 567,
                "image_url": "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
                "stock": 45,
                "features": [
                    "Gốc xi măng",
                    "Chống thấm 2 chiều",
                    "Chống nứt",
                    "Quy cách 20kg",
                    "Phù hợp mái nhà, sân thượng",
                    "Định mức 1.5-2 kg/m²/lớp",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Kova Son Chong Tham - Sơn Chống Thấm Tường",
                "description": (
                    "Sơn chống thấm Kova cho tường ngoài trời, tường nhà vệ sinh, "
                    "hầm xe. Chống thấm ngược, chống ẩm mốc. "
                    "Giá phải chăng cho công trình sửa chữa thấm dột."
                ),
                "price": 680000,
                "category": "Sơn chống thấm",
                "subcategory": "Chống thấm tường",
                "brand": "Kova",
                "rating": 4.4,
                "review_count": 1890,
                "image_url": "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
                "stock": 130,
                "features": [
                    "Chống thấm ngược",
                    "Chống ẩm mốc",
                    "Giá phải chăng",
                    "Quy cách 5L",
                    "Phù hợp tường thấm dột",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Dulux Aquatech Waterproofing - Chống Thấm Ban Công",
                "description": (
                    "Sơn chống thấm Dulux Aquatech chuyên dụng cho ban công, "
                    "sân hiên, tường ngoài trời tiếp xúc nước mưa. "
                    "Lớp màng chống thấm linh hoạt, chống nứt co ngót."
                ),
                "price": 1180000,
                "category": "Sơn chống thấm",
                "subcategory": "Chống thấm ban công",
                "brand": "Dulux",
                "rating": 4.7,
                "review_count": 432,
                "image_url": "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
                "stock": 38,
                "features": [
                    "Màng chống thấm linh hoạt",
                    "Chống nứt co ngót",
                    "Quy cách 5L",
                    "Phù hợp ban công, sân hiên",
                ],
            },
            # ── Bột trét ──────────────────────────────────────────────────
            {
                "id": str(uuid.uuid4()),
                "name": "Dulux Wall Putty - Bột Trét Nội Thất",
                "description": (
                    "Bột trét tường nội thất Dulux Wall Putty tạo bề mặt phẳng mịn "
                    "trước khi sơn lót và sơn phủ. Dễ thi công, khô nhanh, "
                    "bám dính tốt trên tường gạch, bê tông, thạch cao."
                ),
                "price": 280000,
                "category": "Bột trét",
                "subcategory": "Bột trét nội thất",
                "brand": "Dulux",
                "rating": 4.6,
                "review_count": 2780,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 250,
                "features": [
                    "Bề mặt phẳng mịn",
                    "Dễ thi công",
                    "Khô nhanh",
                    "Quy cách 25kg",
                    "Dùng trước sơn lót nội thất",
                    "Định mức 1-1.5 kg/m²/lớp",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Jotun Jotun Putty - Bột Trét Ngoại Thất",
                "description": (
                    "Bột trét ngoại thất Jotun chống nứt, chống thấm, "
                    "tạo nền bề mặt trước khi sơn lót và sơn phủ ngoại thất. "
                    "Phù hợp tường ngoài trời, mặt tiền nhà."
                ),
                "price": 350000,
                "category": "Bột trét",
                "subcategory": "Bột trét ngoại thất",
                "brand": "Jotun",
                "rating": 4.7,
                "review_count": 1560,
                "image_url": "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
                "stock": 180,
                "features": [
                    "Chống nứt",
                    "Chống thấm",
                    "Quy cách 25kg",
                    "Dùng trước sơn ngoại thất",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Kova Bot Tet - Bột Trét Giá Rẻ",
                "description": (
                    "Bột trét tường Kova giá rẻ, phù hợp công trình dân dụng, "
                    "nhà sửa chữa. Tạo bề mặt phẳng trước khi sơn. "
                    "Dùng được cho cả nội thất và ngoại thất."
                ),
                "price": 180000,
                "category": "Bột trét",
                "subcategory": "Bột trét đa năng",
                "brand": "Kova",
                "rating": 4.2,
                "review_count": 5430,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 300,
                "features": [
                    "Giá rẻ",
                    "Đa năng nội/ngoại thất",
                    "Quy cách 25kg",
                    "Dễ thi công",
                ],
            },
            # ── Sơn trang trí / đặc biệt ───────────────────────────────────
            {
                "id": str(uuid.uuid4()),
                "name": "Dulux Effects Metallic - Vàng Đồng Trang Trí",
                "description": (
                    "Sơn trang trí hiệu ứng kim loại Dulux Effects Metallic "
                    "tạo điểm nhấn sang trọng cho tường feature wall phòng khách, "
                    "quầy bar, phòng ngủ. Màu vàng đồng ấm áp."
                ),
                "price": 1850000,
                "category": "Sơn trang trí",
                "subcategory": "Sơn hiệu ứng kim loại",
                "brand": "Dulux",
                "rating": 4.8,
                "review_count": 345,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 25,
                "features": [
                    "Hiệu ứng kim loại",
                    "Feature wall",
                    "Quy cách 1L",
                    "Phù hợp phòng khách sang trọng",
                    "Màu vàng đồng",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Jotun Fenomastic Wonderwall - Xanh Navy",
                "description": (
                    "Sơn trang trí Jotun Fenomastic Wonderwall màu đậm cao cấp, "
                    "độ che phủ xuất sắc trên tường tối màu. "
                    "Màu xanh navy sang trọng cho phòng làm việc, phòng ngủ phong cách tối giản."
                ),
                "price": 1120000,
                "category": "Sơn trang trí",
                "subcategory": "Sơn màu đậm cao cấp",
                "brand": "Jotun",
                "rating": 4.7,
                "review_count": 678,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 40,
                "features": [
                    "Màu đậm che phủ tốt",
                    "Phong cách tối giản",
                    "Quy cách 5L",
                    "Phù hợp phòng làm việc",
                    "Màu xanh navy",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "Nippon Paint Spot-less Matt - Trắng Sáng",
                "description": (
                    "Sơn nội thất Nippon Spot-less Matt chống vết bẩn, "
                    "vết bút bi, vết nước. Lý từng cho phòng trẻ em, "
                    "phòng học tập và khu vực cần giữ sạch thường xuyên."
                ),
                "price": 920000,
                "category": "Sơn nội thất",
                "subcategory": "Sơn chống vết bẩn",
                "brand": "Nippon Paint",
                "rating": 4.6,
                "review_count": 1120,
                "image_url": "https://images.pexels.com/photos/1669754/pexels-photo-1669754.jpeg",
                "stock": 78,
                "features": [
                    "Chống vết bẩn",
                    "Chống vết bút bi",
                    "Hoàn thiện mờ",
                    "Quy cách 5L",
                    "Phù hợp phòng trẻ em, phòng học",
                ],
            },
            {
                "id": str(uuid.uuid4()),
                "name": "MyKolor Son Ngoai That Chiu Thoi Tiet - Cam Đất",
                "description": (
                    "Sơn ngoại thất MyKolor chịu thời tiết giá rẻ, "
                    "chống phai màu cơ bản, phù hợp nhà cấp 4, nhà sửa chữa. "
                    "Màu cam đất ấm áp cho mặt tiền nhà vùng nông thôn."
                ),
                "price": 450000,
                "category": "Sơn ngoại thất",
                "subcategory": "Sơn ngoại thất giá rẻ",
                "brand": "MyKolor",
                "rating": 4.1,
                "review_count": 2340,
                "image_url": "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
                "stock": 165,
                "features": [
                    "Giá rẻ",
                    "Chịu thời tiết cơ bản",
                    "Quy cách 5L",
                    "Phù hợp nhà cấp 4",
                    "Màu cam đất",
                ],
            },
        ]
