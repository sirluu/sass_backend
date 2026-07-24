import logging

from flask import g, request

logger = logging.getLogger(__name__)

TENANT_EXEMPT_PREFIXES = (
    "/api/health",
    "/api/tenants",
)


def resolve_tenant():
    """Resolve current tenant from request headers or host domain."""
    from models.tenant import Tenant

    path = request.path
    if any(path.startswith(prefix) for prefix in TENANT_EXEMPT_PREFIXES):
        g.current_tenant = None
        g.current_tenant_id = None
        return

    tenant = None

    slug = request.headers.get("X-Tenant-Slug")
    if slug:
        tenant = Tenant.query.filter_by(slug=slug, is_active=True).first()

    if not tenant:
        domain = request.headers.get("X-Tenant-Domain")
        if not domain:
            host = request.headers.get("Host", "")
            domain = host.split(":")[0] if host else None

        if domain and domain not in ("localhost", "127.0.0.1"):
            tenant = Tenant.query.filter_by(domain=domain, is_active=True).first()

    if not tenant:
        slug_param = request.args.get("tenant_slug")
        if slug_param:
            tenant = Tenant.query.filter_by(slug=slug_param, is_active=True).first()

    if not tenant:
        from flask import current_app

        default_slug = current_app.config.get("DEFAULT_TENANT_SLUG")
        if default_slug:
            tenant = Tenant.query.filter_by(slug=default_slug, is_active=True).first()

    g.current_tenant = tenant
    g.current_tenant_id = tenant.id if tenant else None


def get_current_tenant_id():
    return getattr(g, "current_tenant_id", None)


def get_current_tenant():
    return getattr(g, "current_tenant", None)
