package com.example.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ホットリロード機能のテスト
 * 
 * **プロパティ4: ホットリロード機能**
 * 任意のテンプレートファイル変更において、開発環境はアプリケーションの再起動なしに変更を反映すべきである
 * 
 * **検証方法: 要件3.3**
 * WHEN テンプレートを変更する場合、開発環境 SHALL ホットリロード機能を提供する
 */
@SpringBootTest
@ActiveProfiles("dev")
class HotReloadTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private SpringTemplateEngine templateEngine;

    /**
     * Thymeleafキャッシュが無効化されていることを検証
     * 開発環境では、テンプレートの変更を即座に反映するためにキャッシュを無効化する必要がある
     */
    @Test
    void testThymeleafCacheDisabled() {
        // Given: 開発環境プロファイルが有効
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        assertThat(activeProfiles).contains("dev");

        // When: Thymeleafの設定を確認
        Boolean cacheEnabled = applicationContext.getEnvironment()
                .getProperty("spring.thymeleaf.cache", Boolean.class);

        // Then: キャッシュが無効化されている
        assertThat(cacheEnabled).isFalse();
    }

    /**
     * Thymeleafテンプレートエンジンが正しく設定されていることを検証
     */
    @Test
    void testThymeleafTemplateEngineConfiguration() {
        // Given: SpringTemplateEngineがBean登録されている
        assertThat(templateEngine).isNotNull();

        // When: テンプレートエンジンの設定を確認
        // Then: テンプレートエンジンが正しく設定されている
        assertThat(templateEngine.getTemplateResolvers()).isNotEmpty();
    }

    /**
     * DevToolsの再起動機能が有効化されていることを検証
     */
    @Test
    void testDevToolsRestartEnabled() {
        // Given: 開発環境プロファイルが有効
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        assertThat(activeProfiles).contains("dev");

        // When: DevToolsの設定を確認
        Boolean restartEnabled = applicationContext.getEnvironment()
                .getProperty("spring.devtools.restart.enabled", Boolean.class);

        // Then: 再起動機能が有効化されている
        assertThat(restartEnabled).isTrue();
    }

    /**
     * DevToolsのLiveReload機能が有効化されていることを検証
     */
    @Test
    void testDevToolsLiveReloadEnabled() {
        // Given: 開発環境プロファイルが有効
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        assertThat(activeProfiles).contains("dev");

        // When: LiveReloadの設定を確認
        Boolean liveReloadEnabled = applicationContext.getEnvironment()
                .getProperty("spring.devtools.livereload.enabled", Boolean.class);

        // Then: LiveReload機能が有効化されている
        assertThat(liveReloadEnabled).isTrue();
    }

    /**
     * DevToolsのポーリング間隔が適切に設定されていることを検証
     * Dockerコンテナでのファイル変更検出を改善するため、適切な間隔が設定されている必要がある
     */
    @Test
    void testDevToolsPollingInterval() {
        // Given: 開発環境プロファイルが有効
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        assertThat(activeProfiles).contains("dev");

        // When: ポーリング間隔の設定を確認
        Integer pollInterval = applicationContext.getEnvironment()
                .getProperty("spring.devtools.restart.poll-interval", Integer.class);

        // Then: ポーリング間隔が設定されている（Dockerコンテナでの検出を改善）
        assertThat(pollInterval).isNotNull();
        assertThat(pollInterval).isGreaterThan(0);
    }

    /**
     * Thymeleafテンプレートのプレフィックスとサフィックスが正しく設定されていることを検証
     */
    @Test
    void testThymeleafTemplateConfiguration() {
        // When: Thymeleafの設定を確認
        String prefix = applicationContext.getEnvironment()
                .getProperty("spring.thymeleaf.prefix");
        String suffix = applicationContext.getEnvironment()
                .getProperty("spring.thymeleaf.suffix");
        String mode = applicationContext.getEnvironment()
                .getProperty("spring.thymeleaf.mode");

        // Then: テンプレートの設定が正しい
        assertThat(prefix).isEqualTo("classpath:/templates/");
        assertThat(suffix).isEqualTo(".html");
        assertThat(mode).isEqualTo("HTML");
    }
}
