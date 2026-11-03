#!/bin/bash
set -e

FILE_NAME=$1
if [ -z "$FILE_NAME" ]; then echo "Usage: $0 <file_name_without_ext>"; exit 1; fi

TABLE_NAME=$(echo "wr_${FILE_NAME}" | tr '[:upper:]' '[:lower:]')
DAT_FILE="/app/data/${FILE_NAME}.dat"
CSV_FILE="/app/data/${FILE_NAME}.csv"
DEF_FILE="/app/data/.def_${FILE_NAME}.conf"
DB_URL="postgresql://${DB_USER}:${PGPASSWORD}@${DB_HOST}:${DB_PORT}/${DB_NAME}"

# 1で事前生成された定義を読み込み
if [ ! -f "$DEF_FILE" ]; then
    echo "[ERROR] 定義ファイルが見つかりません。先に 01_extract_def.sh を実行してください。"
    exit 1
fi
source $DEF_FILE

echo "[INFO] ${DAT_FILE} の文字コード変換およびCSV化を開始..."

LANG=C awk -v widths="$FIELD_WIDTHS" '
  BEGIN {
    # 抽出した幅(例: "3 10 40 7 4")を配列 w に分割し、カラム数を取得
    num_cols = split(widths, w, " ")
    OFS = ","
  }
  {
    # 行末のCRLF(\r)が紛れ込むのを防ぐため除去
    sub(/\r$/, "", $0)

    out = ""
    pos = 1
    for(i=1; i<=num_cols; i++) {
      # substr(対象文字列, 開始位置, 文字数) でバイト単位の切り出し
      val = substr($0, pos, w[i])
      
      if (i == 1) {
        out = val
      } else {
        out = out OFS val
      }
      # 次の項目の開始位置へ進める
      pos += w[i]
    }
    print out
  }
' ${DAT_FILE} | iconv -f SHIFT_JIS -t UTF-8 > ${CSV_FILE}


echo "[INFO] ${TABLE_NAME} へCOPYロードを実行..."
psql $DB_URL -c "\copy ${TABLE_NAME}(${COLUMN_NAMES}) FROM '${CSV_FILE}' WITH CSV;"
echo "[INFO] ロード完了"