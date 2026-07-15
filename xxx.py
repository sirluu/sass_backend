import os
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from sqlalchemy import MetaData

metadata = MetaData(schema=os.getenv("DB_SCHEMA", "systems"))
db = SQLAlchemy(metadata=metadata)
# db = SQLAlchemy()
migrate = Migrate()

 