#!/bin/bash
set -e

echo "[$(date '+%Y-%m-%d %H:%M:%S')] バッチ処理を開始します..."

# DB疎通・クエリ実行
psql -h "$DB_HOST" -U "$DB_USER" -d "$DB_NAME" -c "SELECT current_timestamp;"

echo "[$(date '+%Y-%m-%d %H:%M:%S')] バッチ処理が完了しました。"