CREATE TABLE IF NOT EXISTS tenants (
    tenant_id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tenants (tenant_id, name) VALUES 
('tenant_001', 'テストテナントA'),
('tenant_002', 'テストテナントB')
ON CONFLICT DO NOTHING;