# Hướng dẫn Build & Deploy — Multi-Tenant Paint Store SaaS

Tài liệu này mô tả cách triển khai backend cho **nhiều cửa hàng bán sơn** trên cùng một hệ thống (shared database, row-level isolation).

---

## Kiến trúc triển khai

```
                    ┌─────────────────────────────────────┐
                    │         Frontend (per store)         │
                    │  son-phong-thuy.com / jotun.com ...  │
                    └──────────────┬──────────────────────┘
                                   │ X-Tenant-Slug / Host
                                   ▼
                    ┌─────────────────────────────────────┐
                    │     Flask API (Gunicorn)             │
                    │  resolve_tenant() → g.current_tenant │
                    └──────┬──────────────┬───────────────┘
                           │              │
              ┌────────────▼──┐    ┌──────▼──────────┐
              │  PostgreSQL   │    │    Pinecone      │
              │  (schema:     │    │  namespace =     │
              │   systems)    │    │  tenant.slug     │
              └───────────────┘    └─────────────────┘
```

Mỗi cửa hàng (tenant) có:
- Dữ liệu riêng trong DB (`tenant_id` trên mọi bảng nghiệp vụ)
- Namespace riêng trên Pinecone (theo `slug`, ví dụ `son-jotun-hanoi`)
- Admin và khách hàng riêng (email có thể trùng giữa các cửa hàng)

---

## Yêu cầu hệ thống

| Thành phần | Phiên bản / Ghi chú |
|------------|---------------------|
| Python | 3.12+ |
| PostgreSQL | 14+ (production) |
| Pinecone | Index 1024 dimensions |
| Google AI | Gemini API key |
| RAM | Tối thiểu 2 GB (embedding model nặng) |

---

## 1. Cấu hình môi trường

Sao chép file mẫu và điền giá trị thật:

```bash
cp .env.example .env
```

### Biến môi trường quan trọng

| Biến | Mô tả | Ví dụ |
|------|--------|-------|
| `FLASK_ENV` | `development` hoặc `production` | `production` |
| `DATABASE_URL` | Connection string PostgreSQL | `postgresql://user:pass@host:5432/db` |
| `DB_SCHEMA` | Schema PostgreSQL | `systems` |
| `JWT_SECRET_KEY` | Secret ký JWT (bắt buộc đổi) | chuỗi ngẫu nhiên 32+ ký tự |
| `DEFAULT_TENANT_SLUG` | Cửa hàng mặc định khi không có header | `son-phong-thuy` |
| `GOOGLE_API_KEY` | API key Gemini | — |
| `GEMINI_MODEL` | Model chatbot | `gemini-2.0-flash` |
| `PINECONE_API_KEY` | API key Pinecone | — |
| `PINECONE_INDEX_NAME` | Tên index (1024 dims) | `ecommerce-products` |
| `EMBEDDING_MODEL` | Model embedding | `intfloat/multilingual-e5-large` |
| `EMBEDDING_DIMENSION` | Chiều vector | `1024` |
| `SEED_DATABASE` | Seed dữ liệu test sau migrate | `true` (lần đầu), `false` (prod) |
| `AUTO_INIT_DB` | Tự seed khi dev (first request) | `false` trên production |

---

## 2. Build local (Development)

```bash
# Tạo virtual environment
py -m venv .venv
.venv\Scripts\Activate.ps1          # Windows
# source .venv/bin/activate       # Linux/macOS

pip install -r requirements.txt

# Cấu hình
cp .env.example .env
# Chỉnh sửa .env với API keys

# Migrate + seed dữ liệu test
set FLASK_APP=app.py               # Windows CMD
# export FLASK_APP=app.py          # Linux/macOS
python -m flask db upgrade
python -m scripts.seed_database

# Chạy dev server
python -m flask run --debug
```

Kiểm tra health:

```bash
curl http://localhost:5000/api/health
curl http://localhost:5000/api/tenants/resolve?slug=son-phong-thuy
curl -H "X-Tenant-Slug: son-jotun-hanoi" http://localhost:5000/api/products/
```

---

## 3. Deploy Production

### 3.1 Chuẩn bị PostgreSQL

```sql
CREATE DATABASE sass_paint;
CREATE USER sass_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE sass_paint TO sass_user;
```

Migration tự tạo schema `systems` nếu dùng PostgreSQL (xem `migrations/env.py`).

### 3.2 Chuẩn bị Pinecone

1. Tạo index trên [Pinecone Console](https://app.pinecone.io)
2. **Dimensions:** `1024`
3. **Metric:** `cosine`
4. Một index dùng chung — mỗi cửa hàng là một **namespace** (= `tenant.slug`)

Sau khi seed sản phẩm, re-index nếu cần:

```bash
python -m scripts.index_all_products
```

### 3.3 Deploy với start.sh (Docker / VPS / Render)

```bash
chmod +x start.sh
export FLASK_ENV=production
export DATABASE_URL=postgresql://...
export SEED_DATABASE=false   # Tắt seed sau lần deploy đầu
./start.sh
```

Script `start.sh` thực hiện:
1. `pip install -r requirements.txt`
2. `flask db upgrade`
3. `python -m scripts.seed_database` (nếu `SEED_DATABASE=true`)
4. Khởi động Gunicorn

### 3.4 Deploy thủ công

```bash
pip install -r requirements.txt
export FLASK_APP=app.py
export FLASK_ENV=production
export AUTO_INIT_DB=false

flask db upgrade
python -m scripts.seed_database    # Chỉ lần đầu hoặc staging
gunicorn --config gunicorn.conf.py app:app
```

### 3.5 Docker (ví dụ)

```dockerfile
FROM python:3.12-slim

WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

ENV FLASK_APP=app.py
ENV FLASK_ENV=production
ENV AUTO_INIT_DB=false
ENV SEED_DATABASE=false

EXPOSE 5000
CMD ["./start.sh"]
```

---

## 4. Thêm cửa hàng mới (Onboarding)

### Cách 1: Super Admin API

```bash
# Đăng nhập super admin
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@platform.com","password":"admin123"}'

# Tạo cửa hàng mới
curl -X POST http://localhost:5000/api/tenants/ \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sơn Nippon Cần Thơ",
    "slug": "son-nippon-cantho",
    "domain": "nippon-cantho.example.com",
    "description": "Đại lý Nippon tại Cần Thơ",
    "adminEmail": "admin@nipponcantho.com",
    "adminPassword": "secure-password",
    "adminName": "Admin Cần Thơ",
    "settings": {
      "primaryColor": "#e11d48",
      "currency": "VND",
      "chatbotName": "Tư vấn sơn AI"
    }
  }'
```

### Cách 2: Seed script (dev/staging)

Thêm entry vào `DEFAULT_TENANTS` trong `utils/database_seeder.py`, rồi:

```bash
python -m scripts.seed_database --force
```

### Sau khi tạo cửa hàng

1. Thêm sản phẩm qua API (`POST /api/products/` với header `X-Tenant-Slug`)
2. Hoặc chạy `python -m scripts.index_all_products` để index toàn bộ sản phẩm active

---

## 5. Frontend — Tenant Resolution

Mọi request (trừ `/api/health`, `/api/tenants/*`) cần xác định cửa hàng:

| Cách | Header / Param | Ưu tiên |
|------|----------------|---------|
| Header slug | `X-Tenant-Slug: son-jotun-hanoi` | 1 |
| Header domain | `X-Tenant-Domain: jotun-hanoi.example.com` | 2 |
| Host | `Host: jotun-hanoi.example.com` | 3 |
| Query | `?tenant_slug=son-jotun-hanoi` | 4 |
| Fallback | `DEFAULT_TENANT_SLUG` trong config | 5 |

Ví dụ đăng ký khách hàng tại cửa hàng Jotun Hà Nội:

```bash
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Slug: son-jotun-hanoi" \
  -d '{"name":"Nguyễn Văn A","email":"a@gmail.com","password":"123456"}'
```

---

## 6. Phân quyền (RBAC)

| Role | Mô tả | tenant_id |
|------|--------|-----------|
| `super_admin` | Quản trị nền tảng, tạo/sửa mọi cửa hàng | `NULL` |
| `store_admin` | Quản trị một cửa hàng | ID cửa hàng |
| `customer` | Khách mua sơn | ID cửa hàng |

JWT chứa thêm claims: `role`, `tenant_id`.

---

## 7. Dữ liệu test (Seed)

Sau migrate, chạy seed để có 3 cửa hàng mẫu:

| Cửa hàng | Slug |
|----------|------|
| Sơn Phong Thủy | `son-phong-thuy` |
| Sơn Jotun Hà Nội | `son-jotun-hanoi` |
| Sơn Dulux Đà Nẵng | `son-dulux-danang` |

### Tài khoản test

| Role | Email | Password |
|------|-------|----------|
| Super Admin | `admin@platform.com` | `admin123` |
| Store Admin (Phong Thủy) | `admin@sonphongthuy.com` | `admin123` |
| Store Admin (Jotun HN) | `admin@jotunhanoi.com` | `admin123` |
| Store Admin (Dulux DN) | `admin@duluxdanang.com` | `admin123` |
| Customer | `nguyen.van.a@gmail.com` | `customer123` |

Mỗi cửa hàng có ~27 sản phẩm sơn, giỏ hàng và likes mẫu.

```bash
# Seed lần đầu (bỏ qua nếu đã có dữ liệu)
python -m scripts.seed_database

# Force reseed (ghi đè)
python -m scripts.seed_database --force

# Hoặc qua Flask CLI
flask seed-db
flask seed-db --force
```

---

## 8. Reverse Proxy (Nginx)

Ví dụ nhiều domain trỏ về cùng backend:

```nginx
server {
    listen 80;
    server_name son-phong-thuy.example.com;

    location / {
        proxy_pass http://127.0.0.1:5000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

Backend tự resolve tenant từ `Host` header (trừ localhost).

Hoặc frontend gửi `X-Tenant-Slug` nếu dùng single domain + SPA routing.

---

## 9. Checklist Production

- [ ] Đổi `SECRET_KEY`, `JWT_SECRET_KEY`
- [ ] Dùng PostgreSQL, không dùng SQLite
- [ ] `AUTO_INIT_DB=false`
- [ ] `SEED_DATABASE=false` sau lần seed đầu
- [ ] Pinecone index đúng 1024 dimensions
- [ ] CORS / `FRONTEND_URL` cấu hình đúng domain frontend
- [ ] HTTPS qua reverse proxy
- [ ] Backup PostgreSQL định kỳ
- [ ] Monitor logs tại `logs/ecommerce_chatbot.log`

---

## 10. Troubleshooting

### Migration lỗi trên PostgreSQL

```bash
# Kiểm tra schema
psql $DATABASE_URL -c "SET search_path TO systems; \dt"

# Chạy lại migrate
flask db upgrade
```

### Super admin không đăng nhập được

Super admin (`tenant_id = NULL`) đăng nhập **không cần** header tenant. Email: `admin@platform.com`.

### Sản phẩm search không trả kết quả

- Kiểm tra Pinecone namespace = `tenant.slug`
- Chạy lại: `python -m scripts.index_all_products`

### App không start — SyntaxError

```bash
python -c "from app import create_app; create_app()"
python -m py_compile services/chat_service.py
```

---

## Tài liệu liên quan

- [README.md](../README.md) — API overview & development
- [HLD.md](./HLD.md) — High-level design
- [LLD.md](./LLD.md) — Low-level design
