import json
from datetime import datetime

from models import db


class ChatSession(db.Model):
    __tablename__ = "chat_sessions"

    id = db.Column(db.String(36), primary_key=True)
    tenant_id = db.Column(
        db.String(36), db.ForeignKey("tenants.id"), nullable=False, index=True
    )
    user_id = db.Column(db.String(36), db.ForeignKey("users.id"), nullable=True)
    session_data = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(
        db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )
    is_active = db.Column(db.Boolean, default=True)

    messages = db.relationship(
        "Message", backref="chat_session", lazy=True, cascade="all, delete-orphan"
    )

    def __init__(self, id, tenant_id, user_id=None, session_data=None):
        self.id = id
        self.tenant_id = tenant_id
        self.user_id = user_id
        self.session_data = json.dumps(session_data or {})

    def get_session_data(self):
        try:
            return json.loads(self.session_data) if self.session_data else {}
        except json.JSONDecodeError:
            return {}

    def set_session_data(self, data_dict):
        self.session_data = json.dumps(data_dict)
        self.updated_at = datetime.utcnow()

    def get_message_count(self):
        return len(self.messages)

    def get_recent_messages(self, limit=10):
        from models import Message

        return (
            Message.query.filter_by(chat_session_id=self.id)
            .order_by(Message.created_at.desc())
            .limit(limit)
            .all()
        )

    def to_dict(self, include_messages=False):
        data = {
            "id": self.id,
            "tenantId": self.tenant_id,
            "userId": self.user_id,
            "sessionData": self.get_session_data(),
            "messageCount": self.get_message_count(),
            "createdAt": self.created_at.isoformat() if self.created_at else None,
            "updatedAt": self.updated_at.isoformat() if self.updated_at else None,
            "isActive": self.is_active,
        }
        if include_messages:
            data["messages"] = [msg.to_dict() for msg in self.messages]
        return data

    def __repr__(self):
        return f"<ChatSession {self.id}>"
