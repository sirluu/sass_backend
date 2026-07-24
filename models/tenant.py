import json
from datetime import datetime

from models import db


class Tenant(db.Model):
    __tablename__ = "tenants"

    id = db.Column(db.String(36), primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    slug = db.Column(db.String(100), unique=True, nullable=False, index=True)
    domain = db.Column(db.String(255), unique=True, nullable=True, index=True)
    logo_url = db.Column(db.String(500), nullable=True)
    description = db.Column(db.Text, nullable=True)
    settings = db.Column(db.Text, nullable=True)
    is_active = db.Column(db.Boolean, default=True, index=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(
        db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )

    users = db.relationship("User", backref="tenant", lazy=True)
    products = db.relationship("Product", backref="tenant", lazy=True)

    def get_settings(self):
        try:
            return json.loads(self.settings) if self.settings else {}
        except json.JSONDecodeError:
            return {}

    def set_settings(self, settings_dict):
        self.settings = json.dumps(settings_dict)
        self.updated_at = datetime.utcnow()

    def to_dict(self, include_settings=False):
        data = {
            "id": self.id,
            "name": self.name,
            "slug": self.slug,
            "domain": self.domain,
            "logoUrl": self.logo_url,
            "description": self.description,
            "isActive": self.is_active,
            "createdAt": self.created_at.isoformat() if self.created_at else None,
            "updatedAt": self.updated_at.isoformat() if self.updated_at else None,
        }
        if include_settings:
            data["settings"] = self.get_settings()
        return data

    def __repr__(self):
        return f"<Tenant {self.slug}>"
