package com.example.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * プロジェクト設定の正確性を検証するテスト
 * 
 * **プロパティ1: プロジェクト設定の正確性**
 * 任意のプロジェクト初期化において、build.gradleファイルは指定されたJavaバージョン（Java 17以上）、
 * Spring Boot 3.x以上、Lombok、データベースアクセスライブラリの依存関係を含むべきである
 * 
 * **検証方法: 要件1.1, 1.2, 1.4, 1.5**
 */
class ProjectConfigurationTest {

    private static final String BUILD_GRADLE_PATH = "build.gradle";

    /**
     * Java バージョンが17以上であることを検証
     * **検証対象: 要件1.1**
     */
    @Test
    void shouldUseJava17OrHigher() {
        // 現在実行中のJavaバージョンを確認
        String javaVersion = System.getProperty("java.version");
        String[] versionParts = javaVersion.split("\\.");
        int majorVersion = Integer.parseInt(versionParts[0]);
        
        assertTrue(majorVersion >= 17, 
            String.format("Java バージョンは17以上である必要があります。現在のバージョン: %s", javaVersion));
        
        // build.gradleファイルでJava 17以上が設定されていることを確認
        try {
            String buildGradleContent = Files.readString(Paths.get(BUILD_GRADLE_PATH));
            assertTrue(buildGradleContent.contains("JavaLanguageVersion.of(17)") || 
                      buildGradleContent.contains("JavaLanguageVersion.of(21)") ||
                      buildGradleContent.contains("sourceCompatibility = '17'") ||
                      buildGradleContent.contains("sourceCompatibility = '21'") ||
                      buildGradleContent.contains("sourceCompatibility = JavaVersion.VERSION_17") ||
                      buildGradleContent.contains("sourceCompatibility = JavaVersion.VERSION_21"),
                "build.gradleファイルにJava 17以上の設定が含まれている必要があります");
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * Spring Boot バージョンが3.x以上であることを検証
     * **検証対象: 要件1.2**
     */
    @Test
    void shouldUseSpringBoot3OrHigher() {
        // build.gradleファイルでSpring Boot 3.x以上が設定されていることを確認
        try {
            String buildGradleContent = Files.readString(Paths.get(BUILD_GRADLE_PATH));
            assertTrue(buildGradleContent.contains("org.springframework.boot") && 
                      (buildGradleContent.contains("version '3.") || buildGradleContent.contains("version '4.")),
                "build.gradleファイルにSpring Boot 3.x以上の設定が含まれている必要があります");
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * Lombok依存関係が含まれていることを検証
     * **検証対象: 要件1.4**
     */
    @Test
    void shouldIncludeLombokDependency() {
        try {
            String buildGradleContent = Files.readString(Paths.get(BUILD_GRADLE_PATH));
            
            assertTrue(buildGradleContent.contains("org.projectlombok:lombok"),
                "build.gradleファイルにLombok依存関係が含まれている必要があります");
            
            // compileOnlyとannotationProcessorの両方に設定されていることを確認
            assertTrue(buildGradleContent.contains("compileOnly 'org.projectlombok:lombok'") ||
                      buildGradleContent.contains("compileOnly \"org.projectlombok:lombok\""),
                "LombokがcompileOnlyに設定されている必要があります");
            
            assertTrue(buildGradleContent.contains("annotationProcessor 'org.projectlombok:lombok'") ||
                      buildGradleContent.contains("annotationProcessor \"org.projectlombok:lombok\""),
                "LombokがannotationProcessorに設定されている必要があります");
                
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * データベースアクセスライブラリが含まれていることを検証
     * **検証対象: 要件1.5**
     */
    @Test
    void shouldIncludeDatabaseAccessLibrary() {
        try {
            String buildGradleContent = Files.readString(Paths.get(BUILD_GRADLE_PATH));
            
            // DomaまたはJPAのいずれかが含まれていることを確認
            boolean hasDomaSupport = buildGradleContent.contains("org.seasar.doma") ||
                                   buildGradleContent.contains("doma-spring-boot-starter");
            
            boolean hasJpaSupport = buildGradleContent.contains("spring-boot-starter-data-jpa");
            
            assertTrue(hasDomaSupport || hasJpaSupport,
                "build.gradleファイルにデータベースアクセスライブラリ（DomaまたはJPA）が含まれている必要があります");
            
            // PostgreSQL依存関係も確認
            assertTrue(buildGradleContent.contains("org.postgresql:postgresql"),
                "build.gradleファイルにPostgreSQL依存関係が含まれている必要があります");
                
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * 品質管理ツールが設定されていることを検証
     * **検証対象: 要件5.1, 5.2, 5.3, 5.4**
     */
    @ParameterizedTest
    @ValueSource(strings = {"checkstyle", "jacoco", "spotbugs"})
    void shouldIncludeQualityManagementTools(String toolName) {
        try {
            String buildGradleContent = Files.readString(Paths.get(BUILD_GRADLE_PATH));
            
            switch (toolName) {
                case "checkstyle":
                    assertTrue(buildGradleContent.contains("id 'checkstyle'") ||
                              buildGradleContent.contains("apply plugin: 'checkstyle'"),
                        "CheckStyleプラグインが設定されている必要があります");
                    break;
                case "jacoco":
                    assertTrue(buildGradleContent.contains("id 'jacoco'") ||
                              buildGradleContent.contains("apply plugin: 'jacoco'"),
                        "JaCoCoプラグインが設定されている必要があります");
                    break;
                case "spotbugs":
                    assertTrue(buildGradleContent.contains("com.github.spotbugs") ||
                              buildGradleContent.contains("spotbugs"),
                        "SpotBugsプラグインが設定されている必要があります");
                    break;
            }
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * 必要な設定ファイルが存在することを検証
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "config/checkstyle/checkstyle.xml",
        "config/spotbugs/exclude.xml"
    })
    void shouldHaveRequiredConfigurationFiles(String configFilePath) {
        Path configPath = Paths.get(configFilePath);
        assertTrue(Files.exists(configPath),
            String.format("設定ファイル %s が存在する必要があります", configFilePath));
        
        assertTrue(Files.isReadable(configPath),
            String.format("設定ファイル %s が読み取り可能である必要があります", configFilePath));
    }

    /**
     * Gradleラッパーが正しく設定されていることを検証
     */
    @Test
    void shouldHaveGradleWrapper() {
        // Gradleラッパーファイルの存在確認
        assertTrue(Files.exists(Paths.get("gradlew")),
            "Gradleラッパー（gradlew）が存在する必要があります");
        
        assertTrue(Files.exists(Paths.get("gradlew.bat")),
            "Gradleラッパー（gradlew.bat）が存在する必要があります");
        
        assertTrue(Files.exists(Paths.get("gradle/wrapper/gradle-wrapper.properties")),
            "Gradleラッパープロパティファイルが存在する必要があります");
        
        assertTrue(Files.exists(Paths.get("gradle/wrapper/gradle-wrapper.jar")),
            "Gradleラッパーjarファイルが存在する必要があります");
    }

    /**
     * プロジェクト構造が正しく設定されていることを検証
     */
    @Test
    void shouldHaveCorrectProjectStructure() {
        // 基本的なディレクトリ構造の確認
        String[] requiredDirectories = {
            "src/main/java",
            "src/main/resources",
            "src/test/java"
        };
        
        for (String directory : requiredDirectories) {
            assertTrue(Files.exists(Paths.get(directory)) && Files.isDirectory(Paths.get(directory)),
                String.format("必要なディレクトリ %s が存在する必要があります", directory));
        }
        
        // 設定ファイルの確認
        String[] requiredFiles = {
            "build.gradle",
            "settings.gradle"
        };
        
        for (String file : requiredFiles) {
            assertTrue(Files.exists(Paths.get(file)) && Files.isRegularFile(Paths.get(file)),
                String.format("必要なファイル %s が存在する必要があります", file));
        }
    }
}