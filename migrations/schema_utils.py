import os

from alembic import op

DB_SCHEMA = os.getenv("DB_SCHEMA", "systems")


def get_schema():
    """Return PostgreSQL schema name, or None for SQLite."""
    bind = op.get_bind()
    if bind.dialect.name == "postgresql":
        return DB_SCHEMA
    return None
