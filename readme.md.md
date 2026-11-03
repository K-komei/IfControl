# 店舗データIF取込・検索システム 仕様書

## 1. システム概要
各店舗から日次で送付される売上IF（CSV）ファイルを夜間バッチでPostgreSQLに取り込み、Web画面上で認証されたユーザー（最大5名程度）が検索・閲覧できるシステム。

ポートフォリオ用として過度なインフラ複雑化を避けつつ、**「オンライン処理（AP/UI）」と「データ取り込み（IFバッチ）」の責務分離**をDockerコンテナ環境で実現する。

---

## 2. システム構成・コンポーネント

| コンポーネント | 技術スタック / 採用技術 | 主な役割・責務 |
| :--- | :--- | :--- |
| **フロントエンド** | Vue 3 (Vite, Nginx) | ログイン画面、売上データ検索・一覧表示、取込結果ステータス確認 |
| **バックエンド (AP)** | Spring Boot 3 (Java 21) | 認証・認可（5名限定）、売上データ検索用REST APIの提供 |
| **データベース** | PostgreSQL 16 | データ永続化（UNLOGGED一時テーブル＋月次パーティションテーブル） |
| **IF基盤 (Batch)** | Linuxコンテナ (`postgresql-client`) | 日次夜間バッチ、PostgreSQL `\copy` による高速一括ロード |
| **ファイル配置** | ローカルFTP / ホストマウント | ローカルディレクトリからの `ftp` コマンドによるCSV配置 |

---

## 3. 全体アーキテクチャフロー

```text
[ローカルPC (テストデータ)]
        │
        │  ftp コマンド (PUT)
        ▼
┌───────────────────────────────────────────────┐
│ FTP受信 (またはホスト共有ボリューム)          │
│  - ./data/incoming/ にCSV配置                 │
└───────────────────────┬───────────────────────┘
                        │
                        ▼ (マウント参照)
┌───────────────────────────────────────────────┐
│ IF基盤コンテナ (Linux / バッチ)               │
│  - 翌月パーティションテーブル作成 (DDL)       │
│  - ステージングTRUNCATE                       │
│  - psql \copy で生データ一括ストリームロード  │
│  - ステージング ➔ 本番親テーブルへ流し込み    │
│  - 完了ファイルを ./data/processed/ へ移動   │
└───────────────────────┬───────────────────────┘
                        │ \copy / SQL
                        ▼
┌───────────────────────────────────────────────┐
│ PostgreSQL 16                                 │
│  ├── if_store_daily_raw (UNLOGGED)            │
│  └── store_sales (月次RANGEパーティション)    │
└───────────────────────▲───────────────────────┘
                        │ 参照・検索 (JDBC)
┌───────────────────────┴───────────────────────┐
│ APサーバ (Spring Boot 3)                      │
│  - 認証制御 (5人向けログイン)                 │
│  - 検索API (店舗別・日付別・商品別)           │
└───────────────────────▲───────────────────────┘
                        │ REST API (JSON)
┌───────────────────────┴───────────────────────┐
│ フロントエンド (Vue 3)                        │
│  - 検索・一覧画面                             │
└───────────────────────────────────────────────┘
```

---

## 4. バッチ処理・データ設計のポイント

### (1) DB側テーブル構造
1. **ステージングテーブル (`if_store_daily_raw`)**
   * 高速化のため `UNLOGGED TABLE` を採用。
   * バリデーション前の生データを受け皿として受け取るため、全カラムをVARCHAR形式で定義。
2. **本番売上テーブル (`store_sales`)**
   * 対象日付（`target_date`）をキーにした**月次RANGEパーティショニング**を適用。
   * バッチ内で事前に翌月分のパーティション（`store_sales_YYYYMM`）を自動作成。

### (2) `\copy` による高速ロードの理由
* SQLの `COPY` とは異なり、psqlのメタコマンド `\copy` を採用。
* DBコンテナ側にファイルを直接マウントさせる必要がなく、**バッチコンテナ側がローカルファイルを標準入力ストリームとしてDBに流し込める**ため、コンテナ間の疎結合を維持できる。

### (3) バッチスクリプト処理ステップ
1. 翌月テーブル作成: `CREATE TABLE IF NOT EXISTS store_sales_YYYYMM ...`
2. 一時領域クリア: `TRUNCATE TABLE if_store_daily_raw;`
3. 高速流し込み: `psql -c "\copy if_store_daily_raw FROM 'store.csv' WITH (FORMAT csv, HEADER true);"`
4. 本番反映: `INSERT INTO store_sales ... SELECT store_code, TO_DATE(...), ... FROM if_store_daily_raw;`
5. ファイル退避: `./data/incoming/*.csv` を `./data/processed/` に移動。

---

## 5. 画面・認証設計（シンプル化）

* **アクセス想定**: 5名程度の利用。
* **認証**: Spring Securityを利用し、インメモリまたは軽量テーブルでのアカウント認証。
* **画面機能**:
  * ログイン画面
  * 売上検索画面（店舗コード、日付範囲、商品コードによる絞り込み）
  * 取込ログ・ステータス確認画面（最終バッチ実行時刻、処理件数の確認）

---

## 6. Docker Compose サービス構成（概要）

```yaml
services:
  # データベース
  db:
    image: postgres:16-alpine
    volumes:
      - pgdata:/var/lib/postgresql/data

  # APサーバ (Spring Boot)
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - db

  # フロントエンド (Vue 3 + Nginx)
  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

  # IFバッチ実行用 (夜間実行時のみ起動: profiles設定)
  if-batch:
    build: ./if-batch # Linux + postgresql-client
    volumes:
      - ./data/incoming:/data/incoming
      - ./data/processed:/data/processed
    profiles: ["batch"]
    depends_on:
      - db
```