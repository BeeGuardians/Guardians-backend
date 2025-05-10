#!/bin/sh
export DB_USERNAME=$(cat "$DB_USERNAME_FILE")
export DB_PASSWORD=$(cat "$DB_PASSWORD_FILE")
export MAIL_USERNAME=$(cat "$MAIL_USERNAME_FILE")
export MAIL_PASSWORD=$(cat "$MAIL_PASSWORD_FILE")
export AWS_ACCESS_KEY=$(cat "$AWS_ACCESS_KEY_FILE")
export AWS_SECRET_KEY=$(cat "$AWS_SECRET_KEY_FILE")
export REDIS_PASSWORD=$(cat "$REDIS_PASSWORD_FILE")

exec java -jar app.jar
