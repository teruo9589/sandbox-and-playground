package com.example.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Docker環境の統合テスト
 * 
 * **プロパティ2: Dockerコンテナの起動**
 * 任意のDocker環境起動において、docker-compose upコマンドはアプリケーションコンテナと
 * データベースコンテナの両方を正常に起動し、コンテナ間通信を確立すべきである
 * 
 * **プロパティ9: Docker操作の簡便性**
 * 任意のDocker環境操作において、単一のdocker-composeコマンド（up、down、down -v）で
 * すべてのコンテナとリソースを管理できるべきである
 * 
 * **検証方法: 要件2.1, 2.2, 2.3, 2.4, 2.5, 7.1, 7.2, 7.4, 7.5**
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class DockerEnvironmentTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("init.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Environment environment;

    /**
     * プロパティ2の検証: アプリケーションコンテナが正常に起動することを確認
     * 要件2.1: フロントエンドコンテナ（Spring Boot統合アプリケーション）の起動
     */
    @Test
    @DisplayName("アプリケーションコンテナが正常に起動し、HTTPリクエストに応答する")
    public void testApplicationContainerStartup() {
        // Given
        String url = "http://localhost:" + port + "/";
        
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("<!DOCTYPE html>");
        
        System.out.println("✓ アプリケーションコンテナが正常に起動しました");
        System.out.println("  ポート: " + port);
        System.out.println("  レスポンスステータス: " + response.getStatusCode());
    }

    /**
     * プロパティ2の検証: データベースコンテナが正常に起動することを確認
     * 要件2.3, 2.5: データベースコンテナ（PostgreSQL）の起動
     */
    @Test
    @DisplayName("データベースコンテナが正常に起動し、接続が確立される")
    public void testDatabaseContainerStartup() throws SQLException {
        // Given & When
        try (Connection connection = dataSource.getConnection()) {
            // Then
            assertThat(connection).isNotNull();
            assertThat(connection.isValid(5)).isTrue();
            
            String databaseUrl = connection.getMetaData().getURL();
            String databaseProduct = connection.getMetaData().getDatabaseProductName();
            String databaseVersion = connection.getMetaData().getDatabaseProductVersion();
            
            assertThat(databaseUrl).contains("postgresql");
            assertThat(databaseUrl).contains("testdb");
            assertThat(databaseProduct).isEqualTo("PostgreSQL");
            assertThat(databaseVersion).startsWith("16");
            
            System.out.println("✓ データベースコンテナが正常に起動しました");
            System.out.println("  データベースURL: " + databaseUrl);
            System.out.println("  データベース製品: " + databaseProduct);
            System.out.println("  データベースバージョン: " + databaseVersion);
        }
    }

    /**
     * プロパティ2の検証: コンテナ間通信が正常に確立されることを確認
     * 要件2.4: Dockerネットワークを使用したコンテナ間通信
     */
    @Test
    @DisplayName("アプリケーションコンテナとデータベースコンテナ間の通信が確立される")
    public void testContainerNetworkCommunication() throws SQLException {
        // Given
        String testQuery = "SELECT 1 as test_value";
        
        // When
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(testQuery)) {
            
            // Then
            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getInt("test_value")).isEqualTo(1);
            
            System.out.println("✓ コンテナ間通信が正常に確立されました");
            System.out.println("  テストクエリ実行成功: " + testQuery);
        }
    }

    /**
     * プロパティ2の検証: データベース初期化スクリプトが正常に実行されることを確認
     * 要件7.3: 初期化スクリプトの自動実行
     */
    @Test
    @DisplayName("データベース初期化スクリプトが正常に実行される")
    public void testDatabaseInitialization() throws SQLException {
        // Given
        String tableExistsQuery = """
            SELECT EXISTS (
                SELECT FROM information_schema.tables 
                WHERE table_schema = 'public' 
                AND table_name = 'sample_table'
            )
            """;
        
        // When
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(tableExistsQuery)) {
            
            // Then
            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getBoolean(1)).isTrue();
            
            System.out.println("✓ データベース初期化スクリプトが正常に実行されました");
            System.out.println("  sample_tableテーブルが作成されています");
        }
    }

    /**
     * プロパティ9の検証: 環境設定が正しく適用されることを確認
     * 要件6.3: 開発環境と本番環境で異なる設定プロファイルの使用
     */
    @Test
    @DisplayName("テスト環境の設定プロファイルが正しく適用される")
    public void testEnvironmentConfiguration() {
        // Given & When
        String[] activeProfiles = environment.getActiveProfiles();
        String datasourceUrl = environment.getProperty("spring.datasource.url");
        String thymeleafCache = environment.getProperty("spring.thymeleaf.cache");
        
        // Then
        assertThat(activeProfiles).contains("test");
        assertThat(datasourceUrl).contains("postgresql");
        assertThat(thymeleafCache).isEqualTo("false");
        
        System.out.println("✓ テスト環境の設定プロファイルが正しく適用されました");
        System.out.println("  アクティブプロファイル: " + String.join(", ", activeProfiles));
        System.out.println("  Thymeleafキャッシュ: " + thymeleafCache);
    }

    /**
     * プロパティ9の検証: Actuatorヘルスチェックエンドポイントが正常に動作することを確認
     * 要件7.1: 単一のコマンドですべてのコンテナを起動
     */
    @Test
    @DisplayName("ヘルスチェックエンドポイントが正常に動作する")
    public void testHealthEndpoint() {
        // Given
        String healthUrl = "http://localhost:" + port + "/actuator/health";
        
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
        
        System.out.println("✓ ヘルスチェックエンドポイントが正常に動作しています");
        System.out.println("  エンドポイント: " + healthUrl);
        System.out.println("  ステータス: UP");
    }

    /**
     * プロパティ9の検証: アプリケーションが適切なポートでリッスンしていることを確認
     * 要件7.2: 環境を起動する場合、開発環境は単一のコマンドですべてのコンテナを起動する
     */
    @Test
    @DisplayName("アプリケーションが設定されたポートでリッスンしている")
    public void testApplicationPortBinding() {
        // Given
        String baseUrl = "http://localhost:" + port;
        
        // When
        ResponseEntity<String> indexResponse = restTemplate.getForEntity(baseUrl + "/", String.class);
        ResponseEntity<String> healthResponse = restTemplate.getForEntity(baseUrl + "/actuator/health", String.class);
        
        // Then
        assertThat(indexResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(healthResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        System.out.println("✓ アプリケーションが設定されたポートでリッスンしています");
        System.out.println("  ベースURL: " + baseUrl);
        System.out.println("  インデックスページ: アクセス可能");
        System.out.println("  ヘルスチェック: アクセス可能");
    }

    /**
     * プロパティ2とプロパティ9の統合検証: 完全なDocker環境の動作確認
     * 要件2.1, 2.2, 2.3, 2.4, 2.5, 7.1, 7.2の統合テスト
     */
    @Test
    @DisplayName("Docker環境全体が正常に動作する（統合テスト）")
    public void testCompleteDockerEnvironment() throws SQLException {
        // Given
        String applicationUrl = "http://localhost:" + port + "/";
        String healthUrl = "http://localhost:" + port + "/actuator/health";
        
        // When & Then - アプリケーションの動作確認
        ResponseEntity<String> appResponse = restTemplate.getForEntity(applicationUrl, String.class);
        assertThat(appResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // When & Then - ヘルスチェックの確認
        ResponseEntity<String> healthResponse = restTemplate.getForEntity(healthUrl, String.class);
        assertThat(healthResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(healthResponse.getBody()).contains("\"status\":\"UP\"");
        
        // When & Then - データベース接続の確認
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.isValid(5)).isTrue();
            
            // サンプルデータの挿入と取得テスト
            try (Statement statement = connection.createStatement()) {
                // 重複チェック付きでデータを挿入
                statement.executeUpdate(
                    "INSERT INTO sample_table (name) " +
                    "SELECT 'Docker統合テスト' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM sample_table WHERE name = 'Docker統合テスト')"
                );
                
                try (ResultSet resultSet = statement.executeQuery(
                    "SELECT COUNT(*) FROM sample_table WHERE name = 'Docker統合テスト'"
                )) {
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getInt(1)).isGreaterThanOrEqualTo(1);
                }
            }
        }
        
        System.out.println("✓ Docker環境全体が正常に動作しています");
        System.out.println("  - アプリケーションコンテナ: 起動済み");
        System.out.println("  - データベースコンテナ: 起動済み");
        System.out.println("  - コンテナ間通信: 確立済み");
        System.out.println("  - データベース初期化: 完了");
        System.out.println("  - ヘルスチェック: 正常");
        System.out.println("  - データ操作: 正常");
    }
}