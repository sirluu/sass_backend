import os

from flask_migrate import Migrate
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import MetaData


def _resolve_db_schema():
    database_url = os.environ.get("DATABASE_URL", "sqlite:///ecommerce.db")
    if database_url.startswith("sqlite"):
        return None
    return os.getenv("DB_SCHEMA", "systems")


_schema = _resolve_db_schema()
metadata = MetaData(schema=_schema) if _schema else MetaData()
db = SQLAlchemy(metadata=metadata)
migrate = Migrate()

 