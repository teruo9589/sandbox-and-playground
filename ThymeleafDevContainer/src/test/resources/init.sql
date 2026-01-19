-- データベース初期化スクリプト
-- PostgreSQLコンテナの初回起動時に自動実行されます

-- sample_tableテーブルの作成
CREATE TABLE IF NOT EXISTS sample_table (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- インデックスの作成
CREATE INDEX IF NOT EXISTS idx_sample_table_name ON sample_table(name);

-- サンプルデータの投入（開発環境用）
INSERT INTO sample_table (name) VALUES 
    ('サンプル1'),
    ('サンプル2'),
    ('サンプル3')
ON CONFLICT DO NOTHING;

-- 確認メッセージ（コメントのみ）
-- データベース初期化が完了しました