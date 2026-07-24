"""Seed multi-tenant test data after running migrations.

Usage:
    python -m scripts.seed_database
    python -m scripts.seed_database --force
"""

import argparse
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from app import create_app
from extensions import db
from utils.database_seeder import DatabaseSeeder


def main():
    parser = argparse.ArgumentParser(description="Seed multi-tenant paint store test data")
    parser.add_argument(
        "--force",
        action="store_true",
        help="Force reseed even if data already exists",
    )
    args = parser.parse_args()

    app = create_app()
    with app.app_context():
        seeder = DatabaseSeeder(db)
        seeder.seed_all(force=args.force)
        print("Database seeded successfully.")


if __name__ == "__main__":
    main()
