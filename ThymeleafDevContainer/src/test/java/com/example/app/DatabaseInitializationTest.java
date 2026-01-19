package com.example.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * データベース初期化テスト
 * 
 * **プロパティ10: データベース初期化**
 * 任意の初回起動において、データベースコンテナは初期化スクリプト（init.sql）を自動実行し、
 * 必要なテーブルとデータを作成すべきである
 * 
 * **検証方法: 要件7.3**
 */
@SpringBootTest
@Testcontainers
class DatabaseInitializationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("appdb")
            .withUsername("appuser")
            .withPassword("testpassword")
            .withInitScript("init.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    /**
     * データベース初期化スクリプトが正常に実行されることを検証
     * **検証対象: 要件7.3 - 初回起動時にデータベースを初期化する場合、データベースコンテナ SHALL 初期化スクリプトを自動実行する**
     */
    @Test
    void shouldExecuteInitializationScriptOnFirstStartup() throws SQLException {
        // データベース接続を取得
        try (Connection connection = postgres.createConnection("")) {
            // sample_tableテーブルが作成されていることを確認
            String tableExistsQuery = """
                SELECT EXISTS (
                    SELECT FROM information_schema.tables 
                    WHERE table_schema = 'public' 
                    AND table_name = 'sample_table'
                )
                """;
            
            try (PreparedStatement stmt = connection.prepareStatement(tableExistsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                assertTrue(rs.next(), "クエリ結果が存在する必要があります");
                assertTrue(rs.getBoolean(1), "sample_tableテーブルが作成されている必要があります");
            }
        }
    }

    /**
     * 初期化スクリプトによってテーブル構造が正しく作成されることを検証
     * **検証対象: 要件7.3 - 必要なテーブルの作成**
     */
    @Test
    void shouldCreateCorrectTableStructure() throws SQLException {
        try (Connection connection = postgres.createConnection("")) {
            // テーブルのカラム情報を取得
            String columnQuery = """
                SELECT column_name, data_type, is_nullable, column_default
                FROM information_schema.columns 
                WHERE table_name = 'sample_table' 
                ORDER BY ordinal_position
                """;
            
            try (PreparedStatement stmt = connection.prepareStatement(columnQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                // idカラムの確認
                assertTrue(rs.next(), "idカラムが存在する必要があります");
                assertEquals("id", rs.getString("column_name"));
                assertEquals("bigint", rs.getString("data_type"));
                assertEquals("NO", rs.getString("is_nullable"));
                assertTrue(rs.getString("column_default").contains("nextval"), 
                    "idカラムはSERIAL型（自動採番）である必要があります");
                
                // nameカラムの確認
                assertTrue(rs.next(), "nameカラムが存在する必要があります");
                assertEquals("name", rs.getString("column_name"));
                assertEquals("character varying", rs.getString("data_type"));
                assertEquals("NO", rs.getString("is_nullable"));
                
                // created_atカラムの確認
                assertTrue(rs.next(), "created_atカラムが存在する必要があります");
                assertEquals("created_at", rs.getString("column_name"));
                assertEquals("timestamp without time zone", rs.getString("data_type"));
                assertEquals("NO", rs.getString("is_nullable"));
                assertTrue(rs.getString("column_default").contains("CURRENT_TIMESTAMP"), 
                    "created_atカラムはデフォルト値としてCURRENT_TIMESTAMPを持つ必要があります");
                
                // updated_atカラムの確認
                assertTrue(rs.next(), "updated_atカラムが存在する必要があります");
                assertEquals("updated_at", rs.getString("column_name"));
                assertEquals("timestamp without time zone", rs.getString("data_type"));
                assertEquals("NO", rs.getString("is_nullable"));
                assertTrue(rs.getString("column_default").contains("CURRENT_TIMESTAMP"), 
                    "updated_atカラムはデフォルト値としてCURRENT_TIMESTAMPを持つ必要があります");
            }
        }
    }

    /**
     * 初期化スクリプトによってインデックスが正しく作成されることを検証
     * **検証対象: 要件7.3 - 必要なインデックスの作成**
     */
    @Test
    void shouldCreateRequiredIndexes() throws SQLException {
        try (Connection connection = postgres.createConnection("")) {
            // インデックス情報を取得
            String indexQuery = """
                SELECT indexname, tablename 
                FROM pg_indexes 
                WHERE tablename = 'sample_table' 
                AND indexname = 'idx_sample_table_name'
                """;
            
            try (PreparedStatement stmt = connection.prepareStatement(indexQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                assertTrue(rs.next(), "idx_sample_table_nameインデックスが作成されている必要があります");
                assertEquals("idx_sample_table_name", rs.getString("indexname"));
                assertEquals("sample_table", rs.getString("tablename"));
            }
        }
    }

    /**
     * 初期化スクリプトによってサンプルデータが投入されることを検証
     * **検証対象: 要件7.3 - 初期データの投入**
     */
    @Test
    void shouldInsertInitialSampleData() throws SQLException {
        try (Connection connection = postgres.createConnection("")) {
            // サンプルデータの存在確認
            String dataQuery = "SELECT COUNT(*) as count FROM sample_table";
            
            try (PreparedStatement stmt = connection.prepareStatement(dataQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                assertTrue(rs.next(), "データ件数の取得結果が存在する必要があります");
                int count = rs.getInt("count");
                assertTrue(count >= 3, 
                    String.format("初期データとして最低3件のレコードが投入されている必要があります。実際の件数: %d", count));
            }
            
            // 具体的なサンプルデータの確認
            String sampleDataQuery = "SELECT name FROM sample_table ORDER BY id";
            
            try (PreparedStatement stmt = connection.prepareStatement(sampleDataQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                assertTrue(rs.next(), "サンプル1のデータが存在する必要があります");
                assertEquals("サンプル1", rs.getString("name"));
                
                assertTrue(rs.next(), "サンプル2のデータが存在する必要があります");
                assertEquals("サンプル2", rs.getString("name"));
                
                assertTrue(rs.next(), "サンプル3のデータが存在する必要があります");
                assertEquals("サンプル3", rs.getString("name"));
            }
        }
    }

    /**
     * データベース初期化が冪等性を持つことを検証（重複実行しても問題ないこと）
     * **検証対象: 要件7.3 - 初期化スクリプトの冪等性**
     */
    @Test
    void shouldBeIdempotentInitialization() throws SQLException {
        try (Connection connection = postgres.createConnection("")) {
            // 初期データ件数を確認
            String initialCountQuery = "SELECT COUNT(*) as count FROM sample_table";
            int initialCount;
            try (PreparedStatement stmt = connection.prepareStatement(initialCountQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                assertTrue(rs.next(), "初期データ件数の取得結果が存在する必要があります");
                initialCount = rs.getInt("count");
                assertTrue(initialCount >= 3, "初期データとして最低3件のレコードが存在する必要があります");
            }
            
            // 初期化スクリプトと同じ処理を再実行
            String[] initStatements = {
                """
                CREATE TABLE IF NOT EXISTS sample_table (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """,
                "CREATE INDEX IF NOT EXISTS idx_sample_table_name ON sample_table(name)"
            };
            
            // テーブル作成とインデックス作成のステートメントを実行（エラーが発生しないことを確認）
            for (String statement : initStatements) {
                try (PreparedStatement stmt = connection.prepareStatement(statement)) {
                    stmt.execute();
                }
            }
            
            // データ挿入は名前の重複チェックを行う
            String insertStatement = """
                INSERT INTO sample_table (name) 
                SELECT * FROM (VALUES ('サンプル1'), ('サンプル2'), ('サンプル3')) AS v(name)
                WHERE NOT EXISTS (SELECT 1 FROM sample_table WHERE sample_table.name = v.name)
                """;
            
            try (PreparedStatement stmt = connection.prepareStatement(insertStatement)) {
                stmt.execute();
            }
            
            // データ件数が初期件数と同じであることを確認（重複挿入されていない）
            String finalCountQuery = "SELECT COUNT(*) as count FROM sample_table";
            try (PreparedStatement stmt = connection.prepareStatement(finalCountQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                assertTrue(rs.next(), "最終データ件数の取得結果が存在する必要があります");
                int finalCount = rs.getInt("count");
                assertEquals(initialCount, finalCount, 
                    String.format("冪等性により、データ件数は%d件のままである必要があります。実際の件数: %d", initialCount, finalCount));
            }
        }
    }

    /**
     * データベース接続設定が正しく動作することを検証
     * **検証対象: 要件7.3 - データベース接続の確立**
     */
    @Test
    void shouldEstablishDatabaseConnection() throws SQLException {
        try (Connection connection = postgres.createConnection("")) {
            assertFalse(connection.isClosed(), "データベース接続が確立されている必要があります");
            
            // 基本的なクエリが実行できることを確認
            String testQuery = "SELECT 1 as test_value";
            try (PreparedStatement stmt = connection.prepareStatement(testQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                assertTrue(rs.next(), "テストクエリの結果が存在する必要があります");
                assertEquals(1, rs.getInt("test_value"));
            }
        }
    }
}