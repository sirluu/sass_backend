from datetime import datetime

from models import db


class UserLike(db.Model):
    __tablename__ = "user_likes"
    __table_args__ = (
        db.UniqueConstraint(
            "tenant_id", "user_id", "product_id", name="unique_tenant_user_product_like"
        ),
    )

    id = db.Column(db.String(36), primary_key=True)
    tenant_id = db.Column(
        db.String(36), db.ForeignKey("tenants.id"), nullable=False, index=True
    )
    user_id = db.Column(db.String(36), db.ForeignKey("users.id"), nullable=False)
    product_id = db.Column(db.String(36), db.ForeignKey("products.id"), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

    def to_dict(self):
        return {
            "id": self.id,
            "tenant_id": self.tenant_id,
            "user_id": self.user_id,
            "product_id": self.product_id,
            "created_at": self.created_at.isoformat() if self.created_at else None,
        }
