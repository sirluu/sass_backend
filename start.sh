#!/bin/bash

# Install dependencies
echo "Installing dependencies..."
pip install -r requirements.txt

# Set environment variables for production
export FLASK_ENV=production
export PORT=${PORT:-5000}

# Run database migrations
echo "Running database migrations..."
flask db upgrade

# Seed test data for paint stores (optional, enabled by default on first deploy)
if [ "${SEED_DATABASE:-true}" = "true" ]; then
  echo "Seeding multi-tenant test data..."
  python -m scripts.seed_database
fi

# Start the application with Gunicorn
echo "Starting application on port $PORT..."
exec gunicorn --config gunicorn.conf.py app:app
