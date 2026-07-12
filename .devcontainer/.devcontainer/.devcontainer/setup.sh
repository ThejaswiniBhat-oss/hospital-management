#!/usr/bin/env bash
set -e

echo "==> Installing MySQL server (this takes a minute)..."
sudo apt-get update -qq
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y -qq mysql-server > /dev/null

echo "==> Starting MySQL..."
sudo service mysql start
sleep 5

echo "==> Setting the root password..."
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root'; CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED WITH mysql_native_password BY 'root'; GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION; FLUSH PRIVILEGES;"

echo "==> Creating tables and loading sample data..."
mysql -h 127.0.0.1 -u root -proot < db/schema.sql

echo "==> Downloading Java dependencies (MySQL Connector/J)..."
mvn -q dependency:go-offline || true

echo ""
echo "======================================================="
echo " Setup complete."
echo " Run the app with:   bash run.sh"
echo " Then open port 6080 (Desktop), password: vscode"
echo "======================================================="
