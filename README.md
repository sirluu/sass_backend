# Multi-Tenant Paint Store SaaS — Backend API

Backend Flask cho **nhiều cửa hàng bán sơn** trên cùng một nền tảng. Mỗi cửa hàng có catalog, khách hàng, giỏ hàng và chatbot AI riêng biệt.

Tích hợp **Google Gemini**, **LangChain**, **Pinecone** (vector search theo namespace) và **JWT authentication** với phân quyền multi-tenant.

## Tính năng chính

- **Multi-tenant**: Shared database, cách ly dữ liệu theo `tenant_id` và Pinecone namespace
- **AI Chatbot**: Tư vấn sơn nội/ngoại thất, so sánh sản phẩm, thêm giỏ hàng
- **Vector Search**: Tìm kiếm ngữ nghĩa sản phẩm theo từng cửa hàng
- **E-commerce**: Sản phẩm, giỏ hàng, likes, đăng ký/đăng nhập theo cửa hàng
- **RBAC**: `super_admin`, `store_admin`, `customer`

## Kiến trúc Multi-Tenant

```
Request → X-Tenant-Slug / Host / ?tenant_slug=
       → resolve_tenant() → g.current_tenant_id
       → Services filter theo tenant_id
       → Pinecone query namespace = tenant.slug
```

| Role | Quyền |
|------|-------|
| `super_admin` | Quản lý toàn bộ cửa hàng (`/api/tenants`) |
| `store_admin` | CRUD sản phẩm, cập nhật thông tin cửa hàng |
| `customer` | Mua sắm, chat, giỏ hàng trong một cửa hàng |

## Cài đặt nhanh

### Yêu cầu

- Python 3.12+
- Pinecone account (index 1024 dimensions)
- Google AI Studio API key

### Bước 1: Environment

```bash
py -m venv .venv
.venv\Scripts\Activate.ps1          # Windows
# source .venv/bin/activate         # Linux/macOS

pip install -r requirements.txt
cp .env.example .env
# Chỉnh sửa GOOGLE_API_KEY, PINECONE_API_KEY, JWT_SECRET_KEY
```

### Bước 2: Database & Seed

```bash
set FLASK_APP=app.py                # Windows
python -m flask db upgrade
python -m scripts.seed_database
```

### Bước 3: Chạy

```bash
python -m flask run --debug
# hoặc production:
# ./start.sh
```

### Kiểm tra

```bash
curl http://localhost:5000/api/health
curl "http://localhost:5000/api/tenants/resolve?slug=son-phong-thuy"
curl -H "X-Tenant-Slug: son-jotun-hanoi" http://localhost:5000/api/products/
```

## Tenant Headers

Gửi kèm mọi request (trừ `/api/health`, `/api/tenants/*`):

```http
X-Tenant-Slug: son-jotun-hanoi
```

Hoặc dùng domain (`X-Tenant-Domain`, `Host`) hoặc `?tenant_slug=...`. Fallback: `DEFAULT_TENANT_SLUG` trong `.env`.

## Tài khoản test (sau seed)

| Role | Email | Password | Cửa hàng |
|------|-------|----------|----------|
| Super Admin | `admin@platform.com` | `admin123` | — |
| Store Admin | `admin@sonphongthuy.com` | `admin123` | son-phong-thuy |
| Store Admin | `admin@jotunhanoi.com` | `admin123` | son-jotun-hanoi |
| Store Admin | `admin@duluxdanang.com` | `admin123` | son-dulux-danang |
| Customer | `nguyen.van.a@gmail.com` | `customer123` | son-phong-thuy |

3 cửa hàng mẫu, mỗi cửa hàng ~27 sản phẩm sơn (Dulux, Jotun, Nippon, Kova...).

## API Endpoints

### Stores (Tenants)

- `GET /api/tenants/resolve?slug=&domain=` — Tra cứu cửa hàng (public)
- `GET /api/tenants/current` — Cửa hàng từ request context
- `GET /api/tenants/` — Danh sách cửa hàng (super_admin)
- `POST /api/tenants/` — Tạo cửa hàng mới (super_admin)
- `GET /api/tenants/<id>` — Chi tiết + stats (admin)
- `PUT /api/tenants/<id>` — Cập nhật cửa hàng (store_admin)
- `GET /api/tenants/<id>/stats` — Thống kê cửa hàng

### Authentication

- `POST /api/auth/register` — Đăng ký (cần tenant context)
- `POST /api/auth/login` — Đăng nhập
- `POST /api/auth/refresh` — Refresh token
- `GET /api/auth/me` — Thông tin user hiện tại
- `PUT /api/auth/preferences` — Cập nhật preferences
- `POST /api/auth/deactivate` — Vô hiệu hóa tài khoản

### Products *(cần `X-Tenant-Slug`)*

- `GET /api/products/` — Danh sách + filter
- `GET /api/products/<id>` — Chi tiết sản phẩm
- `POST /api/products/search` — Semantic search
- `GET /api/products/recommendations` — Gợi ý sản phẩm
- `GET /api/products/categories` — Danh mục
- `GET /api/products/brands` — Thương hiệu
- `GET /api/products/stats` — Thống kê
- `POST /api/products/` — Tạo sản phẩm (store_admin)
- `PUT /api/products/<id>` — Cập nhật (store_admin)
- `DELETE /api/products/<id>` — Xóa (store_admin)

### Cart & Likes *(cần tenant + JWT)*

- `GET /api/cart/<user_id>` — Giỏ hàng
- `POST /api/cart/add` — Thêm vào giỏ
- `DELETE /api/cart/remove` — Xóa khỏi giỏ
- `PUT /api/cart/update` — Cập nhật số lượng
- `DELETE /api/cart/clear` — Xóa toàn bộ giỏ
- `POST /api/likes/toggle` — Like/unlike
- `GET /api/likes/user/<user_id>` — Sản phẩm đã like
- `GET /api/likes/popular` — Sản phẩm phổ biến

### Chat *(cần `X-Tenant-Slug`)*

- `POST /api/chat/message` — Gửi tin nhắn chatbot
- `GET /api/chat/history/<session_id>` — Lịch sử chat
- `GET /api/chat/sessions` — Danh sách session
- `DELETE /api/chat/sessions/<id>` — Xóa session
- `GET /api/chat/health` — Health check chat service

### System

- `GET /api/health` — Health check API

## Database Models

| Model | Mô tả |
|-------|--------|
| **Tenant** | Cửa hàng: name, slug, domain, settings (JSON) |
| **User** | User theo tenant; unique `(tenant_id, email)` |
| **Product** | Sản phẩm sơn theo tenant |
| **Cart** | Giỏ hàng theo tenant + user |
| **UserLike** | Yêu thích theo tenant |
| **ChatSession / Message** | Lịch sử chat theo tenant |

## Scripts hữu ích

```bash
# Seed dữ liệu test (tenants, users, products, cart, likes)
python -m scripts.seed_database
python -m scripts.seed_database --force

# Re-index Pinecone cho tất cả cửa hàng
python -m scripts.index_all_products

# Flask CLI
flask seed-db
flask seed-db --force
```

## Deploy Production

Xem hướng dẫn chi tiết tại **[docs/DEPLOYMENT.md](docs/DEPLOYMENT.md)**:

- PostgreSQL + schema `systems`
- Gunicorn via `start.sh`
- Pinecone namespace per store
- Onboarding cửa hàng mới qua Super Admin API
- Checklist bảo mật production

## Cấu trúc thư mục

```
├── app.py                 # App factory, tenant middleware
├── config.py              # Config + DEFAULT_TENANT_SLUG
├── models/                # Tenant, User, Product, Cart, Chat...
├── routes/                # Blueprints (auth, tenants, products, chat...)
├── services/              # Business logic + AI/vector
├── utils/                 # tenant_context, decorators, seeder
├── migrations/            # Alembic migrations
├── scripts/               # seed_database, index_all_products
└── docs/                  # DEPLOYMENT.md, HLD.md, LLD.md
```

## Troubleshooting

| Vấn đề | Giải pháp |
|--------|-----------|
| Pinecone lỗi kết nối | Kiểm tra API key, index 1024 dims |
| Search không có kết quả | Chạy `python -m scripts.index_all_products` |
| Login sai cửa hàng | Gửi đúng `X-Tenant-Slug` hoặc dùng super_admin |
| Migration lỗi PostgreSQL | Kiểm tra `DB_SCHEMA=systems`, quyền DB |

## License

MIT License
