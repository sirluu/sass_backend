import os

from config import config
from dotenv import load_dotenv
from flask import Flask, jsonify
from flask_cors import CORS
from flask_jwt_extended import JWTManager
from extensions import db, migrate
from utils.logger_config import setup_logging

load_dotenv()


jwt = JWTManager()


def create_app(config_name=None):
    if config_name is None:
        config_name = os.environ.get("FLASK_ENV", "development")

    app = Flask(__name__)
    app.config.from_object(config[config_name])

    db_uri = app.config.get("SQLALCHEMY_DATABASE_URI", "")
    if db_uri.startswith("postgresql"):
        schema = app.config.get("DB_SCHEMA", "systems")
        app.config["SQLALCHEMY_ENGINE_OPTIONS"] = {
            "connect_args": {"options": f"-csearch_path={schema},public"}
        }

    db.init_app(app)
    jwt.init_app(app)
    migrate.init_app(app, db)

    CORS(app, resources={r"/*": {"origins": "*"}}, supports_credentials=True)
    setup_logging(app)

    from routes import register_routes

    register_routes(app)

    from utils.tenant_context import resolve_tenant

    @app.before_request
    def set_tenant_context():
        resolve_tenant()

    @app.errorhandler(404)
    def not_found(error):
        return jsonify({"success": False, "message": "Resource not found"}), 404

    @app.errorhandler(500)
    def internal_error(error):
        app.logger.error(f"Internal server error: {str(error)}")
        return jsonify({"success": False, "message": "Internal server error"}), 500

    @app.errorhandler(400)
    def bad_request(error):
        return jsonify({"success": False, "message": "Bad request"}), 400

    @jwt.expired_token_loader
    def expired_token_callback(jwt_header, jwt_payload):
        return jsonify({"success": False, "message": "Token has expired"}), 401

    @jwt.invalid_token_loader
    def invalid_token_callback(error):
        return jsonify({"success": False, "message": "Invalid token"}), 401

    @jwt.unauthorized_loader
    def missing_token_callback(error):
        return jsonify(
            {"success": False, "message": "Authorization token is required"}
        ), 401

    @app.route("/api/health", methods=["GET"])
    def health_check():
        return jsonify(
            {
                "success": True,
                "status": "healthy",
                "message": "E-commerce Chatbot API is running",
            }
        ), 200

    _register_database_initializer(app)
    _register_cli_commands(app)

    return app


def _register_cli_commands(app):
    """Register Flask CLI commands for database seeding."""
    import click

    @app.cli.command("seed-db")
    @click.option("--force", is_flag=True, help="Force reseed even if data exists")
    def seed_db_command(force):
        """Seed multi-tenant test data (tenants, users, products, cart, likes)."""
        from utils.database_seeder import DatabaseSeeder

        seeder = DatabaseSeeder(db)
        seeder.seed_all(force=force)
        click.echo("Database seeded successfully.")


def _register_database_initializer(app):
    """Initialize database once on startup in development."""

    @app.before_request
    def initialize_database():
        app.before_request_funcs[None].remove(initialize_database)

        auto_init = os.environ.get("AUTO_INIT_DB", "true").lower() == "true"
        if not auto_init:
            return

        try:
            if app.config.get("DEBUG"):
                db.create_all()

            from utils.database_seeder import DatabaseSeeder

            seeder = DatabaseSeeder(db)
            seeder.seed_all()
            app.logger.info("Database initialized successfully")
        except Exception as e:
            app.logger.error(f"Error initializing database: {str(e)}")


app = create_app()

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    debug = os.environ.get("FLASK_ENV", "development") == "development"

    app.run(
        host="0.0.0.0",
        port=port,
        debug=debug,
        threaded=True,
    )
