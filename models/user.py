import json
from datetime import datetime

from models import db
from werkzeug.security import check_password_hash, generate_password_hash

USER_ROLES = ("super_admin", "store_admin", "customer")


class User(db.Model):
    __tablename__ = "users"
    __table_args__ = (
        db.UniqueConstraint("tenant_id", "email", name="unique_tenant_email"),
    )

    id = db.Column(db.String(36), primary_key=True)
    tenant_id = db.Column(
        db.String(36), db.ForeignKey("tenants.id"), nullable=True, index=True
    )
    email = db.Column(db.String(120), nullable=False, index=True)
    name = db.Column(db.String(100), nullable=False)
    password_hash = db.Column(db.String(255), nullable=False)
    role = db.Column(db.String(20), default="customer", nullable=False, index=True)
    preferences = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(
        db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )
    is_active = db.Column(db.Boolean, default=True)

    chat_sessions = db.relationship(
        "ChatSession", backref="user", lazy=True, cascade="all, delete-orphan"
    )

    def __init__(
        self, id, email, name, password=None, tenant_id=None, role="customer"
    ):
        self.id = id
        self.email = email
        self.name = name
        self.tenant_id = tenant_id
        self.role = role if role in USER_ROLES else "customer"
        if password:
            self.set_password(password)
        self.preferences = json.dumps(
            {"favoriteCategories": [], "priceRange": [0, 2000], "favoriteBrands": []}
        )

    def set_password(self, password):
        self.password_hash = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password_hash, password)

    def get_preferences(self):
        try:
            return json.loads(self.preferences) if self.preferences else {}
        except json.JSONDecodeError:
            return {}

    def set_preferences(self, preferences_dict):
        self.preferences = json.dumps(preferences_dict)
        self.updated_at = datetime.utcnow()

    def is_admin(self):
        return self.role in ("store_admin", "super_admin")

    def to_dict(self):
        data = {
            "id": self.id,
            "email": self.email,
            "name": self.name,
            "role": self.role,
            "tenantId": self.tenant_id,
            "preferences": self.get_preferences(),
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None,
            "is_active": self.is_active,
        }
        if self.tenant:
            data["tenant"] = self.tenant.to_dict()
        return data

    def __repr__(self):
        return f"<User {self.email}>"
