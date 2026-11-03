#!/bin/bash
set -e

# 受信ファイル名（拡張子抜き）
TARGET_FILE="TNP_URAG_DATA" 

echo "=== バッチ処理開始: ${TARGET_FILE} ==="

# ロード実行
/app/scripts/02_load_data.sh $TARGET_FILE

# Spring Boot APIキック
# === 一時的にコメントアウト ===
# API_URL="http://dev-backend:8080/api/batch/validate"
# HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "${API_URL}?target=${TARGET_FILE}")
# if [ "$HTTP_STATUS" -ne 200 ]; then ...
#    echo "[ERROR] BackendのバリデーションAPIでエラーが発生しました (HTTP: ${HTTP_STATUS})"
#    exit 1
#fi

# 事後処理（退避・削除）
/app/scripts/03_finalize.sh $TARGET_FILE

echo "=== バッチ処理完了: ${TARGET_FILE} ==="