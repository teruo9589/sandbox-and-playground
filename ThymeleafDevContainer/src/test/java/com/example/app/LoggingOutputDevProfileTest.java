package com.example.app;

import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 開発環境プロファイルでのログ出力テスト
 * 
 * **プロパティ6: ログ出力**
 * 開発環境での設定されたログレベルでの出力を検証
 * 
 * **検証方法: 要件4.5**
 */
@SpringBootTest
@ActiveProfiles("dev")
@Testcontainers
class LoggingOutputDevProfileTest {

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
    }

    /**
     * 開発環境プロファイルでのログレベル設定を検証
     * **検証対象: 要件4.5 - 開発環境でのログレベル（DEBUG）**
     */
    @Test
    @DisplayName("開発環境プロファイルでのログレベル設定が正しい")
    void shouldHaveCorrectLogLevelForDevProfile() {
        // Given
        Logger comExampleLogger = (Logger) LoggerFactory.getLogger("com.example");
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        
        // When & Then
        // com.exampleパッケージのログレベルがDEBUG以下であることを確認
        assertThat(comExampleLogger.isDebugEnabled()).isTrue();
        
        // ルートロガーのログレベルがINFO以下であることを確認
        assertThat(rootLogger.isInfoEnabled()).isTrue();
        
        System.out.println("✓ 開発環境プロファイルでのログレベル設定が正しく適用されています");
        System.out.println("  com.exampleパッケージ - DEBUGレベル有効: " + comExampleLogger.isDebugEnabled());
        System.out.println("  ルートロガー - INFOレベル有効: " + rootLogger.isInfoEnabled());
    }
}