from app import create_app
from extensions import db
from sqlalchemy import text

app = create_app()
with app.app_context():
    db.session.execute(text('CREATE SCHEMA IF NOT EXISTS systems'))
    db.session.execute(
        text('CREATE TABLE IF NOT EXISTS systems._migration_test (id serial primary key)')
    )
    db.session.commit()
    rows = db.session.execute(
        text("SELECT tablename FROM pg_tables WHERE schemaname='systems' ORDER BY tablename")
    ).fetchall()
    print("systems tables after manual create:", rows)
    db.session.execute(text("DROP TABLE IF EXISTS systems._migration_test"))
    db.session.commit()
