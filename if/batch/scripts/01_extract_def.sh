#!/bin/bash
set -e

FILE_NAME=$1
if [ -z "$FILE_NAME" ]; then echo "Usage: $0 <file_name_without_ext>"; exit 1; fi

TABLE_NAME=$(echo "wr_${FILE_NAME}" | tr '[:upper:]' '[:lower:]')
DB_URL="postgresql://${DB_USER}:${PGPASSWORD}@${DB_HOST}:${DB_PORT}/${DB_NAME}"
DEF_FILE="/app/data/.def_${FILE_NAME}.conf"

COLS_INFO=$(psql $DB_URL -t -A -F ',' -c "
  SELECT column_name, character_maximum_length 
  FROM information_schema.columns 
  WHERE table_name = '${TABLE_NAME}' 
    AND column_name NOT IN ('seq_id', 'status', 'error_message', 'created_at', 'updated_at') 
  ORDER BY ordinal_position;
")

FIELD_WIDTHS=$(echo "$COLS_INFO" | awk -F',' '{printf "%s ", $2}' | sed 's/ $//')
COLUMN_NAMES=$(echo "$COLS_INFO" | awk -F',' '{printf "%s,", $1}' | sed 's/,$//')

echo "FIELD_WIDTHS=\"${FIELD_WIDTHS}\"" > $DEF_FILE
echo "COLUMN_NAMES=\"${COLUMN_NAMES}\"" >> $DEF_FILE

echo "[INFO] ${TABLE_NAME} の定義を抽出・配置しました -> ${DEF_FILE}"