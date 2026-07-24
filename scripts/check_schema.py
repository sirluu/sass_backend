from app import create_app
from extensions import db
from sqlalchemy import text

app = create_app()
with app.app_context():
    rows = db.session.execute(
        text(
            "SELECT schemaname, tablename FROM pg_tables "
            "WHERE tablename IN ('tenants','users','products','cart','alembic_version') "
            "ORDER BY schemaname, tablename"
        )
    ).fetchall()
    print("app tables:", rows)

    schemas = db.session.execute(
        text("SELECT schema_name FROM information_schema.schemata ORDER BY schema_name")
    ).fetchall()
    print("schemas:", [s[0] for s in schemas])

    try:
        v = db.session.execute(text('SELECT version_num FROM systems.alembic_version')).fetchall()
        print("systems.alembic_version:", v)
    except Exception as e:
        print("systems alembic error:", e)

    try:
        v = db.session.execute(text("SELECT version_num FROM alembic_version")).fetchall()
        print("public alembic_version:", v)
    except Exception as e:
        print("public alembic error:", e)
