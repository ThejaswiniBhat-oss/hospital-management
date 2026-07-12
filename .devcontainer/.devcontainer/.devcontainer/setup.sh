#!/usr/bin/env bash
set -e

echo "==> Installing MySQL command-line client..."
sudo apt-get update -qq
sudo apt-get install -y -qq default-mysql-client > /dev/null

echo "==> Waiting for the MySQL server to start..."
for i in $(seq 1 60); do
    if mysqladmin ping -h 127.0.0.1 -u root -proot --silent 2>/dev/null; then
        echo "    MySQL is up."
        break
    fi
    sleep 2
done

echo "==> Creating tables and loading sample data..."
mysql -h 127.0.0.1 -u root -proot < db/schema.sql

echo "==> Downloading Java dependencies (MySQL Connector/J)..."
mvn -q dependency:go-offline || true

echo ""
echo "======================================================="
echo " Setup complete."
echo " Run the app with:   bash run.sh"
echo " Then open the 'Desktop' port (6080), password: vscode"
echo "======================================================="
