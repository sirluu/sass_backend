"""Force reseed paint products into DB and Pinecone.

Usage:
    python -m scripts.reseed_products
"""
from app import create_app
from utils.database_seeder import DatabaseSeeder
from xxx import db


def main():
    app = create_app()
    with app.app_context():
        seeder = DatabaseSeeder(db)
        seeder.seed_products(force=True)
        print("Done: paint products reseeded and indexed to Pinecone.")


if __name__ == "__main__":
    main()
