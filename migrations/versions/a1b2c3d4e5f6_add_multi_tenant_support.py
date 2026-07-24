"""add multi tenant support

Revision ID: a1b2c3d4e5f6
Revises: c42517d8354b
Create Date: 2026-07-24 08:45:00.000000

"""
import sys
from pathlib import Path

from alembic import op
import sqlalchemy as sa

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from schema_utils import get_schema


revision = "a1b2c3d4e5f6"
down_revision = "c42517d8354b"
branch_labels = None
depends_on = None

DEFAULT_TENANT_ID = "tenant-son-phong-thuy"


def upgrade():
    schema = get_schema()

    op.create_table(
        "tenants",
        sa.Column("id", sa.String(length=36), nullable=False),
        sa.Column("name", sa.String(length=200), nullable=False),
        sa.Column("slug", sa.String(length=100), nullable=False),
        sa.Column("domain", sa.String(length=255), nullable=True),
        sa.Column("logo_url", sa.String(length=500), nullable=True),
        sa.Column("description", sa.Text(), nullable=True),
        sa.Column("settings", sa.Text(), nullable=True),
        sa.Column("is_active", sa.Boolean(), nullable=True),
        sa.Column("created_at", sa.DateTime(), nullable=True),
        sa.Column("updated_at", sa.DateTime(), nullable=True),
        sa.PrimaryKeyConstraint("id"),
        schema=schema,
    )
    with op.batch_alter_table("tenants", schema=schema) as batch_op:
        batch_op.create_index(batch_op.f("ix_tenants_slug"), ["slug"], unique=True)
        batch_op.create_index(batch_op.f("ix_tenants_domain"), ["domain"], unique=True)
        batch_op.create_index(batch_op.f("ix_tenants_is_active"), ["is_active"], unique=False)

    tenant_table = f'"{schema}".tenants' if schema else "tenants"
    op.execute(
        sa.text(
            f"""
            INSERT INTO {tenant_table}
                (id, name, slug, domain, description, settings, is_active, created_at, updated_at)
            VALUES (
                :id,
                'Sơn Phong Thủy',
                'son-phong-thuy',
                NULL,
                'Default paint store',
                '{{"primaryColor":"#2563eb","currency":"VND","chatbotName":"Tư vấn sơn AI"}}',
                true,
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            """
        ).bindparams(id=DEFAULT_TENANT_ID)
    )

    with op.batch_alter_table("users", schema=schema) as batch_op:
        batch_op.add_column(sa.Column("tenant_id", sa.String(length=36), nullable=True))
        batch_op.add_column(
            sa.Column("role", sa.String(length=20), nullable=False, server_default="customer")
        )
        batch_op.create_foreign_key("fk_users_tenant_id", "tenants", ["tenant_id"], ["id"])
        batch_op.create_index(batch_op.f("ix_users_tenant_id"), ["tenant_id"], unique=False)
        batch_op.create_index(batch_op.f("ix_users_role"), ["role"], unique=False)
        batch_op.drop_index("ix_users_email")
        batch_op.create_index(batch_op.f("ix_users_email"), ["email"], unique=False)
        batch_op.create_unique_constraint("unique_tenant_email", ["tenant_id", "email"])

    users_table = f'"{schema}".users' if schema else "users"
    op.execute(
        sa.text(
            f"UPDATE {users_table} SET tenant_id = :tenant_id WHERE tenant_id IS NULL"
        ).bindparams(tenant_id=DEFAULT_TENANT_ID)
    )

    with op.batch_alter_table("products", schema=schema) as batch_op:
        batch_op.add_column(sa.Column("tenant_id", sa.String(length=36), nullable=True))
        batch_op.create_foreign_key(
            "fk_products_tenant_id", "tenants", ["tenant_id"], ["id"]
        )
        batch_op.create_index(batch_op.f("ix_products_tenant_id"), ["tenant_id"], unique=False)

    products_table = f'"{schema}".products' if schema else "products"
    op.execute(
        sa.text(
            f"UPDATE {products_table} SET tenant_id = :tenant_id WHERE tenant_id IS NULL"
        ).bindparams(tenant_id=DEFAULT_TENANT_ID)
    )

    with op.batch_alter_table("products", schema=schema) as batch_op:
        batch_op.alter_column("tenant_id", existing_type=sa.String(length=36), nullable=False)

    with op.batch_alter_table("cart", schema=schema) as batch_op:
        batch_op.add_column(sa.Column("tenant_id", sa.String(length=36), nullable=True))
        batch_op.create_foreign_key("fk_cart_tenant_id", "tenants", ["tenant_id"], ["id"])
        batch_op.create_index(batch_op.f("ix_cart_tenant_id"), ["tenant_id"], unique=False)

    cart_table = f'"{schema}".cart' if schema else "cart"
    op.execute(
        sa.text(
            f"UPDATE {cart_table} SET tenant_id = :tenant_id WHERE tenant_id IS NULL"
        ).bindparams(tenant_id=DEFAULT_TENANT_ID)
    )

    with op.batch_alter_table("cart", schema=schema) as batch_op:
        batch_op.alter_column("tenant_id", existing_type=sa.String(length=36), nullable=False)
        batch_op.create_unique_constraint(
            "unique_tenant_user_cart_item", ["tenant_id", "user_id", "product_id"]
        )

    with op.batch_alter_table("chat_sessions", schema=schema) as batch_op:
        batch_op.add_column(sa.Column("tenant_id", sa.String(length=36), nullable=True))
        batch_op.create_foreign_key(
            "fk_chat_sessions_tenant_id", "tenants", ["tenant_id"], ["id"]
        )
        batch_op.create_index(
            batch_op.f("ix_chat_sessions_tenant_id"), ["tenant_id"], unique=False
        )

    chat_sessions_table = f'"{schema}".chat_sessions' if schema else "chat_sessions"
    op.execute(
        sa.text(
            f"UPDATE {chat_sessions_table} SET tenant_id = :tenant_id WHERE tenant_id IS NULL"
        ).bindparams(tenant_id=DEFAULT_TENANT_ID)
    )

    with op.batch_alter_table("chat_sessions", schema=schema) as batch_op:
        batch_op.alter_column("tenant_id", existing_type=sa.String(length=36), nullable=False)

    with op.batch_alter_table("user_likes", schema=schema) as batch_op:
        batch_op.add_column(sa.Column("tenant_id", sa.String(length=36), nullable=True))
        batch_op.create_foreign_key(
            "fk_user_likes_tenant_id", "tenants", ["tenant_id"], ["id"]
        )
        batch_op.create_index(
            batch_op.f("ix_user_likes_tenant_id"), ["tenant_id"], unique=False
        )
        batch_op.drop_constraint("unique_user_product_like", type_="unique")
        batch_op.create_unique_constraint(
            "unique_tenant_user_product_like", ["tenant_id", "user_id", "product_id"]
        )

    user_likes_table = f'"{schema}".user_likes' if schema else "user_likes"
    op.execute(
        sa.text(
            f"UPDATE {user_likes_table} SET tenant_id = :tenant_id WHERE tenant_id IS NULL"
        ).bindparams(tenant_id=DEFAULT_TENANT_ID)
    )

    with op.batch_alter_table("user_likes", schema=schema) as batch_op:
        batch_op.alter_column("tenant_id", existing_type=sa.String(length=36), nullable=False)


def downgrade():
    schema = get_schema()

    with op.batch_alter_table("user_likes", schema=schema) as batch_op:
        batch_op.drop_constraint("unique_tenant_user_product_like", type_="unique")
        batch_op.create_unique_constraint(
            "unique_user_product_like", ["user_id", "product_id"]
        )
        batch_op.drop_index(batch_op.f("ix_user_likes_tenant_id"))
        batch_op.drop_constraint("fk_user_likes_tenant_id", type_="foreignkey")
        batch_op.drop_column("tenant_id")

    with op.batch_alter_table("chat_sessions", schema=schema) as batch_op:
        batch_op.drop_index(batch_op.f("ix_chat_sessions_tenant_id"))
        batch_op.drop_constraint("fk_chat_sessions_tenant_id", type_="foreignkey")
        batch_op.drop_column("tenant_id")

    with op.batch_alter_table("cart", schema=schema) as batch_op:
        batch_op.drop_constraint("unique_tenant_user_cart_item", type_="unique")
        batch_op.drop_index(batch_op.f("ix_cart_tenant_id"))
        batch_op.drop_constraint("fk_cart_tenant_id", type_="foreignkey")
        batch_op.drop_column("tenant_id")

    with op.batch_alter_table("products", schema=schema) as batch_op:
        batch_op.drop_index(batch_op.f("ix_products_tenant_id"))
        batch_op.drop_constraint("fk_products_tenant_id", type_="foreignkey")
        batch_op.drop_column("tenant_id")

    with op.batch_alter_table("users", schema=schema) as batch_op:
        batch_op.drop_constraint("unique_tenant_email", type_="unique")
        batch_op.drop_index(batch_op.f("ix_users_role"))
        batch_op.drop_index(batch_op.f("ix_users_tenant_id"))
        batch_op.drop_constraint("fk_users_tenant_id", type_="foreignkey")
        batch_op.drop_column("role")
        batch_op.drop_column("tenant_id")
        batch_op.drop_index(batch_op.f("ix_users_email"))
        batch_op.create_index(batch_op.f("ix_users_email"), ["email"], unique=True)

    with op.batch_alter_table("tenants", schema=schema) as batch_op:
        batch_op.drop_index(batch_op.f("ix_tenants_is_active"))
        batch_op.drop_index(batch_op.f("ix_tenants_domain"))
        batch_op.drop_index(batch_op.f("ix_tenants_slug"))

    op.drop_table("tenants", schema=schema)
