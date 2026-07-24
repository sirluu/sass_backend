from sqlalchemy import text

from app import create_app
from extensions import db
from flask_migrate import upgrade

app = create_app()
with app.app_context():
    print("Before upgrade:")
    before = db.session.execute(
        text("SELECT tablename FROM pg_tables WHERE schemaname='systems' ORDER BY tablename")
    ).fetchall()
    print(before)

    upgrade()

    print("After upgrade:")
    after = db.session.execute(
        text("SELECT schemaname, tablename FROM pg_tables WHERE schemaname NOT IN ('pg_catalog','information_schema') ORDER BY schemaname, tablename")
    ).fetchall()
    for row in after:
        print(" ", row)
