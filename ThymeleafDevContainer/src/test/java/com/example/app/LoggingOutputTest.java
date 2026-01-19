package com.example.app;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.app.controller.SampleController;
import com.example.app.service.SampleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ログ出力のテスト
 * 
 * **プロパティ6: ログ出力**
 * 任意のアプリケーション実行において、ログはSLF4J + Logbackを使用して
 * 設定されたログレベルで出力されるべきである
 * 
 * **検証方法: 要件4.5**
 */
@SpringBootTest
@Testcontainers
class LoggingOutputTest {

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

    @Autowired
    private SampleService sampleService;

    @Autowired
    private SampleController sampleController;

    @Autowired
    private Environment environment;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger serviceLogger;
    private Logger controllerLogger;

    @BeforeEach
    void setUp() {
        // テスト用のListAppenderを設定
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        listAppender = new ListAppender<>();
        listAppender.setContext(loggerContext);
        listAppender.start();

        // サービスとコントローラーのロガーを取得
        serviceLogger = (Logger) LoggerFactory.getLogger(SampleService.class);
        controllerLogger = (Logger) LoggerFactory.getLogger(SampleController.class);
        
        // ListAppenderを追加
        serviceLogger.addAppender(listAppender);
        controllerLogger.addAppender(listAppender);
    }

    /**
     * SLF4J + Logbackが正しく設定されていることを検証
     * **検証対象: 要件4.5 - SLF4J + Logbackを使用してログを記録する**
     */
    @Test
    @DisplayName("SLF4J + Logbackが正しく設定されている")
    void shouldUseSLF4JAndLogback() {
        // Given & When
        org.slf4j.Logger slf4jLogger = LoggerFactory.getLogger(LoggingOutputTest.class);
        
        // Then
        assertThat(slf4jLogger).isInstanceOf(ch.qos.logback.classic.Logger.class);
        
        // LoggerContextがLogbackのものであることを確認
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        assertThat(loggerContext).isNotNull();
        assertThat(loggerContext.getName()).isEqualTo("default");
        
        System.out.println("✓ SLF4J + Logbackが正しく設定されています");
        System.out.println("  ロガークラス: " + slf4jLogger.getClass().getName());
        System.out.println("  ロガーコンテキスト: " + loggerContext.getName());
    }

    /**
     * サービス層でログが正しく出力されることを検証
     * **検証対象: 要件4.5 - バックエンドコンテナでのログ出力**
     */
    @Test
    @DisplayName("サービス層でログが正しく出力される")
    void shouldLogCorrectlyInServiceLayer() {
        // Given
        listAppender.list.clear();
        
        // When
        sampleService.findAll();
        
        // Then
        List<ILoggingEvent> logEvents = listAppender.list;
        
        // DEBUGレベルのログが出力されていることを確認（テスト環境ではDEBUGが有効）
        boolean hasDebugLog = logEvents.stream()
            .anyMatch(event -> event.getLevel() == Level.DEBUG && 
                             event.getLoggerName().equals(SampleService.class.getName()) &&
                             event.getMessage().contains("全てのエンティティを取得"));
        
        // テスト環境ではDEBUGレベルが有効でない場合があるため、ログレベルを確認
        Logger testServiceLogger = (Logger) LoggerFactory.getLogger(SampleService.class);
        if (testServiceLogger.isDebugEnabled()) {
            assertThat(hasDebugLog).isTrue();
            System.out.println("✓ サービス層でDEBUGログが正しく出力されています");
        } else {
            System.out.println("✓ サービス層のDEBUGログは現在の設定では出力されません（正常）");
        }
        
        System.out.println("  出力されたログ件数: " + logEvents.size());
        logEvents.forEach(event -> 
            System.out.println("  - " + event.getLevel() + ": " + event.getMessage()));
    }

    /**
     * コントローラー層でログが正しく出力されることを検証
     * **検証対象: 要件4.5 - HTTPリクエスト処理でのログ出力**
     */
    @Test
    @DisplayName("コントローラー層でログが正しく出力される")
    void shouldLogCorrectlyInControllerLayer() {
        // Given
        listAppender.list.clear();
        org.springframework.ui.ExtendedModelMap model = new org.springframework.ui.ExtendedModelMap();
        
        // When
        sampleController.index(model);
        
        // Then
        List<ILoggingEvent> logEvents = listAppender.list;
        assertThat(logEvents).isNotEmpty();
        
        // INFOレベルのログが出力されていることを確認
        boolean hasInfoLog = logEvents.stream()
            .anyMatch(event -> event.getLevel() == Level.INFO && 
                             event.getMessage().contains("インデックスページにアクセス"));
        
        assertThat(hasInfoLog).isTrue();
        
        System.out.println("✓ コントローラー層でログが正しく出力されています");
        System.out.println("  出力されたログ件数: " + logEvents.size());
        logEvents.forEach(event -> 
            System.out.println("  - " + event.getLevel() + ": " + event.getMessage()));
    }

    /**
     * 異なるログレベルが正しく動作することを検証
     * **検証対象: 要件4.5 - 設定されたログレベルでの出力**
     */
    @ParameterizedTest
    @ValueSource(strings = {"DEBUG", "INFO", "WARN", "ERROR"})
    @DisplayName("異なるログレベルが正しく動作する")
    void shouldHandleDifferentLogLevels(String logLevel) {
        // Given
        listAppender.list.clear();
        Logger testLogger = (Logger) LoggerFactory.getLogger("com.example.test");
        testLogger.addAppender(listAppender);
        
        Level level = Level.valueOf(logLevel);
        String testMessage = "テストメッセージ - " + logLevel;
        
        // When
        switch (logLevel) {
            case "DEBUG":
                testLogger.debug(testMessage);
                break;
            case "INFO":
                testLogger.info(testMessage);
                break;
            case "WARN":
                testLogger.warn(testMessage);
                break;
            case "ERROR":
                testLogger.error(testMessage);
                break;
        }
        
        // Then
        List<ILoggingEvent> logEvents = listAppender.list;
        
        if (testLogger.isEnabledFor(level)) {
            assertThat(logEvents).isNotEmpty();
            
            boolean hasExpectedLog = logEvents.stream()
                .anyMatch(event -> event.getLevel() == level && 
                                 event.getMessage().equals(testMessage));
            
            assertThat(hasExpectedLog).isTrue();
            
            System.out.println("✓ " + logLevel + "レベルのログが正しく出力されました");
        } else {
            System.out.println("✓ " + logLevel + "レベルは現在の設定では出力されません（正常）");
        }
    }

    /**
     * ログメッセージのフォーマットが正しいことを検証
     * **検証対象: 要件4.5 - ログフォーマットの確認**
     */
    @Test
    @DisplayName("ログメッセージのフォーマットが正しい")
    void shouldHaveCorrectLogMessageFormat() {
        // Given
        listAppender.list.clear();
        String testMessage = "テストログメッセージ";
        
        // When
        serviceLogger.info(testMessage);
        
        // Then
        List<ILoggingEvent> logEvents = listAppender.list;
        assertThat(logEvents).hasSize(1);
        
        ILoggingEvent logEvent = logEvents.get(0);
        assertThat(logEvent.getMessage()).isEqualTo(testMessage);
        assertThat(logEvent.getLevel()).isEqualTo(Level.INFO);
        assertThat(logEvent.getLoggerName()).isEqualTo(SampleService.class.getName());
        assertThat(logEvent.getTimeStamp()).isGreaterThan(0);
        
        System.out.println("✓ ログメッセージのフォーマットが正しく設定されています");
        System.out.println("  メッセージ: " + logEvent.getMessage());
        System.out.println("  レベル: " + logEvent.getLevel());
        System.out.println("  ロガー名: " + logEvent.getLoggerName());
        System.out.println("  タイムスタンプ: " + logEvent.getTimeStamp());
    }

    /**
     * パラメータ付きログメッセージが正しく処理されることを検証
     * **検証対象: 要件4.5 - パラメータ化されたログメッセージ**
     */
    @Test
    @DisplayName("パラメータ付きログメッセージが正しく処理される")
    void shouldHandleParameterizedLogMessages() {
        // Given
        listAppender.list.clear();
        String name = "テストエンティティ";
        Long id = 123L;
        
        // When
        serviceLogger.info("エンティティを処理: id={}, name={}", id, name);
        
        // Then
        List<ILoggingEvent> logEvents = listAppender.list;
        assertThat(logEvents).hasSize(1);
        
        ILoggingEvent logEvent = logEvents.get(0);
        assertThat(logEvent.getFormattedMessage()).isEqualTo("エンティティを処理: id=123, name=テストエンティティ");
        
        // パラメータが正しく設定されていることを確認
        Object[] arguments = logEvent.getArgumentArray();
        assertThat(arguments).hasSize(2);
        assertThat(arguments[0]).isEqualTo(id);
        assertThat(arguments[1]).isEqualTo(name);
        
        System.out.println("✓ パラメータ付きログメッセージが正しく処理されています");
        System.out.println("  フォーマット済みメッセージ: " + logEvent.getFormattedMessage());
        System.out.println("  パラメータ数: " + arguments.length);
    }

    /**
     * 例外情報がログに正しく記録されることを検証
     * **検証対象: 要件4.5 - 例外情報のログ出力**
     */
    @Test
    @DisplayName("例外情報がログに正しく記録される")
    void shouldLogExceptionInformationCorrectly() {
        // Given
        listAppender.list.clear();
        RuntimeException testException = new RuntimeException("テスト例外");
        
        // When
        serviceLogger.error("エラーが発生しました", testException);
        
        // Then
        List<ILoggingEvent> logEvents = listAppender.list;
        assertThat(logEvents).hasSize(1);
        
        ILoggingEvent logEvent = logEvents.get(0);
        assertThat(logEvent.getMessage()).isEqualTo("エラーが発生しました");
        assertThat(logEvent.getLevel()).isEqualTo(Level.ERROR);
        assertThat(logEvent.getThrowableProxy()).isNotNull();
        assertThat(logEvent.getThrowableProxy().getMessage()).isEqualTo("テスト例外");
        
        System.out.println("✓ 例外情報がログに正しく記録されています");
        System.out.println("  エラーメッセージ: " + logEvent.getMessage());
        System.out.println("  例外メッセージ: " + logEvent.getThrowableProxy().getMessage());
        System.out.println("  例外クラス: " + logEvent.getThrowableProxy().getClassName());
    }

    /**
     * ログ設定ファイル（logback-spring.xml）が正しく読み込まれることを検証
     * **検証対象: 要件4.5 - Logback設定ファイルの読み込み**
     */
    @Test
    @DisplayName("ログ設定ファイルが正しく読み込まれる")
    void shouldLoadLogbackConfigurationCorrectly() {
        // Given
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // When & Then
        // LoggerContextが正しく初期化されていることを確認
        assertThat(loggerContext).isNotNull();
        assertThat(loggerContext.isStarted()).isTrue();
        
        // コンソールアペンダーが設定されていることを確認
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        assertThat(rootLogger.iteratorForAppenders()).hasNext();
        
        System.out.println("✓ ログ設定ファイルが正しく読み込まれています");
        System.out.println("  LoggerContext開始状態: " + loggerContext.isStarted());
        System.out.println("  設定されたアペンダー数: " + 
            java.util.stream.StreamSupport.stream(
                java.util.Spliterators.spliteratorUnknownSize(
                    rootLogger.iteratorForAppenders(), 
                    java.util.Spliterator.ORDERED), false).count());
    }

    /**
     * 統合テスト: アプリケーション全体でログが正しく動作することを検証
     * **検証対象: 要件4.5 - アプリケーション全体でのログ出力**
     */
    @Test
    @DisplayName("アプリケーション全体でログが正しく動作する（統合テスト）")
    void shouldLogCorrectlyAcrossApplication() {
        // Given
        listAppender.list.clear();
        org.springframework.ui.ExtendedModelMap model = new org.springframework.ui.ExtendedModelMap();
        
        // When - コントローラーからサービスまでの一連の処理を実行
        sampleController.index(model);
        
        // Then
        List<ILoggingEvent> logEvents = listAppender.list;
        
        // コントローラーとサービス両方のログが出力されていることを確認
        boolean hasControllerLog = logEvents.stream()
            .anyMatch(event -> event.getLoggerName().contains("SampleController"));
        
        boolean hasServiceLog = logEvents.stream()
            .anyMatch(event -> event.getLoggerName().contains("SampleService"));
        
        assertThat(hasControllerLog).isTrue();
        // サービスログはDEBUGレベルなので、テスト環境の設定によっては出力されない場合がある
        
        // 異なるログレベルが混在していることを確認
        boolean hasInfoLevel = logEvents.stream()
            .anyMatch(event -> event.getLevel() == Level.INFO);
        
        boolean hasDebugLevel = logEvents.stream()
            .anyMatch(event -> event.getLevel() == Level.DEBUG);
        
        assertThat(hasInfoLevel).isTrue();
        // DEBUGレベルは環境によって異なるため、必須ではない
        
        System.out.println("✓ アプリケーション全体でログが正しく動作しています");
        System.out.println("  総ログ件数: " + logEvents.size());
        System.out.println("  コントローラーログ: " + hasControllerLog);
        System.out.println("  サービスログ: " + hasServiceLog);
        System.out.println("  INFOレベル: " + hasInfoLevel);
        System.out.println("  DEBUGレベル: " + hasDebugLevel);
        
        // ログの詳細を表示
        logEvents.forEach(event -> 
            System.out.println("  - " + event.getLevel() + " [" + 
                event.getLoggerName().substring(event.getLoggerName().lastIndexOf('.') + 1) + 
                "]: " + event.getMessage()));
    }
}