import logging
from logging.config import fileConfig
import os
from flask import current_app

from alembic import context
from sqlalchemy import text


SCHEMA = os.getenv("DB_SCHEMA", "systems")

config = context.config

fileConfig(config.config_file_name)
logger = logging.getLogger("alembic.env")


def get_engine():
    try:
        return current_app.extensions["migrate"].db.get_engine()
    except (TypeError, AttributeError):
        return current_app.extensions["migrate"].db.engine


def get_engine_url():
    try:
        return get_engine().url.render_as_string(hide_password=False).replace("%", "%%")
    except AttributeError:
        return str(get_engine().url).replace("%", "%%")


def is_postgresql():
    return get_engine().dialect.name == "postgresql"


config.set_main_option("sqlalchemy.url", get_engine_url())
target_db = current_app.extensions["migrate"].db


def get_metadata():
    if hasattr(target_db, "metadatas"):
        return target_db.metadatas[None]
    return target_db.metadata


def configure_context(connection, **extra):
    configure_kwargs = {
        "connection": connection,
        "target_metadata": get_metadata(),
        "compare_type": True,
        **extra,
    }

    if is_postgresql():
        configure_kwargs.update(
            {
                "include_schemas": True,
                "version_table_schema": SCHEMA,
                "default_schema_name": SCHEMA,
            }
        )

    context.configure(**configure_kwargs)


def run_migrations_offline():
    url = config.get_main_option("sqlalchemy.url")
    offline_kwargs = {
        "url": url,
        "target_metadata": get_metadata(),
        "literal_binds": True,
        "compare_type": True,
    }

    if url.startswith("postgresql"):
        offline_kwargs.update(
            {
                "include_schemas": True,
                "version_table_schema": SCHEMA,
                "default_schema_name": SCHEMA,
            }
        )

    context.configure(**offline_kwargs)
    with context.begin_transaction():
        context.run_migrations()


def run_migrations_online():
    def process_revision_directives(context, revision, directives):
        if getattr(config.cmd_opts, "autogenerate", False):
            script = directives[0]
            if script.upgrade_ops.is_empty():
                directives[:] = []
                logger.info("No changes in schema detected.")

    conf_args = current_app.extensions["migrate"].configure_args
    if conf_args.get("process_revision_directives") is None:
        conf_args["process_revision_directives"] = process_revision_directives

    connectable = get_engine()

    with connectable.connect() as connection:
        if is_postgresql():
            connection.execute(text(f'CREATE SCHEMA IF NOT EXISTS "{SCHEMA}"'))
            connection.execute(text(f'SET search_path TO "{SCHEMA}", public'))
            connection.dialect.default_schema_name = SCHEMA
            conf_args["include_schemas"] = True
            conf_args["version_table_schema"] = SCHEMA

        configure_context(connection, **conf_args)

        with context.begin_transaction():
            context.run_migrations()


if context.is_offline_mode():
    run_migrations_offline()
else:
    run_migrations_online()
