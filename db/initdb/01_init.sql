CREATE TABLE wr_tnp_urag_data (
    seq_id SERIAL PRIMARY KEY,
    
    -- IFのバイト長(SJIS)をそのまま桁数として定義
    branch_code VARCHAR(3),      -- 3byte: 支店番号
    transaction_id VARCHAR(10),  -- 10byte: 取引番号
    item_name VARCHAR(40),       -- 40byte: 商品名（全半角混在、右スペース埋め）
    sales_price VARCHAR(7),      -- 7byte: 売却価格
    discount_rate VARCHAR(4),    -- 4byte: 割引係数
    
    status VARCHAR(20) DEFAULT 'UNPROCESSED',
    error_message TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- (参考) 将来的にBackendが移行させる本番テーブル
CREATE TABLE sales_transactions (
    transaction_id VARCHAR(10) NOT NULL,
    branch_code VARCHAR(3) NOT NULL,
    item_name VARCHAR(40) NOT NULL,
    sales_price INTEGER NOT NULL,
    discount_rate NUMERIC(3, 2),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);