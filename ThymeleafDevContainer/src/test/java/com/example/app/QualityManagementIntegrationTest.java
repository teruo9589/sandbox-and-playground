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
 * 品質管理ツールの統合テスト
 * 
 * **プロパティ7: 品質管理ツールの統合**
 * 任意のビルド実行において、Gradleビルドシステムは品質管理ツール（CheckStyle、JaCoCo、SpotBugs）を
 * 実行可能であり、品質チェックが失敗した場合はビルドを失敗させるべきである
 * 
 * **検証方法: 要件5.1, 5.2, 5.3, 5.4, 5.5**
 */
class QualityManagementIntegrationTest {

    private static final String PROJECT_ROOT = System.getProperty("user.dir");

    /**
     * CheckStyle設定が正しく構成されていることを検証
     * **検証対象: 要件5.1**
     */
    @Test
    void shouldHaveCheckStyleConfiguration() {
        try {
            String buildGradleContent = Files.readString(Paths.get(PROJECT_ROOT, "build.gradle"));
            
            // CheckStyleプラグインが設定されていることを確認
            assertTrue(buildGradleContent.contains("id 'checkstyle'"),
                "CheckStyleプラグインが設定されている必要があります");
            
            // CheckStyle設定が含まれていることを確認
            assertTrue(buildGradleContent.contains("checkstyle {"),
                "CheckStyle設定ブロックが含まれている必要があります");
            
            // toolVersionが設定されていることを確認
            assertTrue(buildGradleContent.contains("toolVersion = '10.") || 
                      buildGradleContent.contains("toolVersion = \"10."),
                "CheckStyleのバージョンが設定されている必要があります");
            
            // configFileが設定されていることを確認
            assertTrue(buildGradleContent.contains("configFile = file("),
                "CheckStyle設定ファイルのパスが設定されている必要があります");
            
            // ignoreFailuresがfalseに設定されていることを確認
            assertTrue(buildGradleContent.contains("ignoreFailures = false"),
                "CheckStyleの失敗時にビルドを停止する設定が必要です");
            
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * JaCoCo設定が正しく構成されていることを検証
     * **検証対象: 要件5.2**
     */
    @Test
    void shouldHaveJaCoCoConfiguration() {
        try {
            String buildGradleContent = Files.readString(Paths.get(PROJECT_ROOT, "build.gradle"));
            
            // JaCoCoプラグインが設定されていることを確認
            assertTrue(buildGradleContent.contains("id 'jacoco'"),
                "JaCoCoプラグインが設定されている必要があります");
            
            // JaCoCo設定が含まれていることを確認
            assertTrue(buildGradleContent.contains("jacoco {"),
                "JaCoCo設定ブロックが含まれている必要があります");
            
            // toolVersionが設定されていることを確認
            assertTrue(buildGradleContent.contains("toolVersion = \"0.8.") ||
                      buildGradleContent.contains("toolVersion = '0.8."),
                "JaCoCoのバージョンが設定されている必要があります");
            
            // jacocoTestReportが設定されていることを確認
            assertTrue(buildGradleContent.contains("jacocoTestReport {"),
                "JaCoCoテストレポート設定が含まれている必要があります");
            
            // jacocoTestCoverageVerificationが設定されていることを確認
            assertTrue(buildGradleContent.contains("jacocoTestCoverageVerification {"),
                "JaCoCoカバレッジ検証設定が含まれている必要があります");
            
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * SpotBugs設定が正しく構成されていることを検証
     * **検証対象: 要件5.3**
     */
    @Test
    void shouldHaveSpotBugsConfiguration() {
        try {
            String buildGradleContent = Files.readString(Paths.get(PROJECT_ROOT, "build.gradle"));
            
            // SpotBugsプラグインが設定されていることを確認
            assertTrue(buildGradleContent.contains("com.github.spotbugs"),
                "SpotBugsプラグインが設定されている必要があります");
            
            // SpotBugs設定が含まれていることを確認
            assertTrue(buildGradleContent.contains("spotbugs {"),
                "SpotBugs設定ブロックが含まれている必要があります");
            
            // toolVersionが設定されていることを確認
            assertTrue(buildGradleContent.contains("toolVersion = '4.") ||
                      buildGradleContent.contains("toolVersion = \"4."),
                "SpotBugsのバージョンが設定されている必要があります");
            
            // ignoreFailuresがfalseに設定されていることを確認
            assertTrue(buildGradleContent.contains("ignoreFailures = false"),
                "SpotBugsの失敗時にビルドを停止する設定が必要です");
            
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * 品質管理ツールがビルドプロセスに統合されていることを検証
     * **検証対象: 要件5.4**
     */
    @Test
    void shouldIntegrateQualityToolsInBuildProcess() {
        try {
            String buildGradleContent = Files.readString(Paths.get(PROJECT_ROOT, "build.gradle"));
            
            // qualityCheckタスクが定義されていることを確認
            assertTrue(buildGradleContent.contains("task qualityCheck"),
                "qualityCheckタスクが定義されている必要があります");
            
            // qualityCheckタスクが品質管理ツールに依存していることを確認
            assertTrue(buildGradleContent.contains("dependsOn 'checkstyleMain'") &&
                      buildGradleContent.contains("dependsOn") &&
                      (buildGradleContent.contains("'spotbugsMain'") || buildGradleContent.contains("'jacocoTestCoverageVerification'")),
                "qualityCheckタスクが品質管理ツールに依存している必要があります");
            
            // buildタスクがqualityCheckに依存していることを確認
            assertTrue(buildGradleContent.contains("tasks.named('build')") &&
                      buildGradleContent.contains("dependsOn qualityCheck"),
                "buildタスクがqualityCheckに依存している必要があります");
            
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * 品質チェック失敗時にビルドが失敗する設定があることを検証
     * **検証対象: 要件5.5**
     */
    @Test
    void shouldFailBuildWhenQualityCheckFails() {
        try {
            String buildGradleContent = Files.readString(Paths.get(PROJECT_ROOT, "build.gradle"));
            
            // CheckStyleのignoreFailuresがfalseに設定されていることを確認
            assertTrue(buildGradleContent.contains("ignoreFailures = false"),
                "CheckStyleの失敗時にビルドを停止する設定が必要です");
            
            // SpotBugsのignoreFailuresがfalseに設定されていることを確認（SpotBugs設定内で）
            String[] lines = buildGradleContent.split("\n");
            boolean inSpotBugsBlock = false;
            boolean hasIgnoreFailuresFalse = false;
            
            for (String line : lines) {
                if (line.trim().startsWith("spotbugs {")) {
                    inSpotBugsBlock = true;
                } else if (inSpotBugsBlock && line.trim().equals("}")) {
                    inSpotBugsBlock = false;
                } else if (inSpotBugsBlock && line.contains("ignoreFailures = false")) {
                    hasIgnoreFailuresFalse = true;
                    break;
                }
            }
            
            assertTrue(hasIgnoreFailuresFalse,
                "SpotBugsの失敗時にビルドを停止する設定が必要です");
            
            // JaCoCoカバレッジ検証がfinalizedByで設定されていることを確認
            assertTrue(buildGradleContent.contains("finalizedBy jacocoTestCoverageVerification"),
                "JaCoCoカバレッジ検証がテスト後に実行される設定が必要です");
            
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * 品質管理ツールの設定ファイルが正しく読み込まれることを検証
     */
    @ParameterizedTest
    @ValueSource(strings = {"checkstyle", "spotbugs"})
    void shouldLoadQualityToolConfigurations(String toolName) {
        Path configPath;
        
        switch (toolName) {
            case "checkstyle":
                configPath = Paths.get(PROJECT_ROOT, "config", "checkstyle", "checkstyle.xml");
                break;
            case "spotbugs":
                configPath = Paths.get(PROJECT_ROOT, "config", "spotbugs", "exclude.xml");
                break;
            default:
                fail("未知の品質管理ツール: " + toolName);
                return;
        }
        
        // 設定ファイルが存在し、読み取り可能であることを確認
        assertTrue(Files.exists(configPath), 
            String.format("%s設定ファイルが存在する必要があります: %s", toolName, configPath));
        assertTrue(Files.isReadable(configPath), 
            String.format("%s設定ファイルが読み取り可能である必要があります: %s", toolName, configPath));
        
        // 設定ファイルが空でないことを確認
        try {
            String content = Files.readString(configPath);
            assertFalse(content.trim().isEmpty(), 
                String.format("%s設定ファイルが空でない必要があります", toolName));
        } catch (IOException e) {
            fail(String.format("%s設定ファイルの読み込みに失敗しました: %s", toolName, e.getMessage()));
        }
    }

    /**
     * JaCoCoカバレッジ基準が設定されていることを検証
     */
    @Test
    void shouldHaveJaCoCoCoverageThresholds() {
        try {
            String buildGradleContent = Files.readString(Paths.get(PROJECT_ROOT, "build.gradle"));
            
            // JaCoCoカバレッジ検証設定が含まれていることを確認
            assertTrue(buildGradleContent.contains("jacocoTestCoverageVerification"),
                "JaCoCoカバレッジ検証設定が含まれている必要があります");
            
            // カバレッジ基準が設定されていることを確認
            assertTrue(buildGradleContent.contains("minimum") && buildGradleContent.contains("COVEREDRATIO"),
                "JaCoCoカバレッジ基準が設定されている必要があります");
            
            // LINE カバレッジ基準の確認
            assertTrue(buildGradleContent.contains("counter = 'LINE'"),
                "LINEカバレッジ基準が設定されている必要があります");
            
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * 品質管理ツールのレポート出力設定が正しく構成されていることを検証
     */
    @Test
    void shouldHaveCorrectReportOutputConfiguration() {
        try {
            String buildGradleContent = Files.readString(Paths.get(PROJECT_ROOT, "build.gradle"));
            
            // CheckStyleレポート設定の確認
            assertTrue(buildGradleContent.contains("reports {") && 
                      (buildGradleContent.contains("xml.required = true") || buildGradleContent.contains("html.required = true")),
                "CheckStyleレポート設定が含まれている必要があります");
            
            // JaCoCoレポート設定の確認
            assertTrue(buildGradleContent.contains("jacocoTestReport {") &&
                      buildGradleContent.contains("reports {"),
                "JaCoCoレポート設定が含まれている必要があります");
            
            // SpotBugsレポート設定の確認
            assertTrue(buildGradleContent.contains("SpotBugsTask") || 
                      buildGradleContent.contains("reports {"),
                "SpotBugsレポート設定が含まれている必要があります");
            
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * Gradleタスクの依存関係が正しく設定されていることを検証
     */
    @Test
    void shouldHaveCorrectTaskDependencies() {
        try {
            String buildGradleContent = Files.readString(Paths.get(PROJECT_ROOT, "build.gradle"));
            
            // testタスクがjacocoTestReportで終了することを確認
            assertTrue(buildGradleContent.contains("tasks.named('test')") &&
                      buildGradleContent.contains("finalizedBy jacocoTestReport"),
                "testタスクがjacocoTestReportで終了する必要があります");
            
            // jacocoTestReportがjacocoTestCoverageVerificationで終了することを確認
            assertTrue(buildGradleContent.contains("finalizedBy jacocoTestCoverageVerification"),
                "jacocoTestReportがjacocoTestCoverageVerificationで終了する必要があります");
            
            // buildタスクがqualityCheckに依存していることを確認
            assertTrue(buildGradleContent.contains("tasks.named('build')") &&
                      buildGradleContent.contains("dependsOn qualityCheck"),
                "buildタスクがqualityCheckに依存している必要があります");
            
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * 品質管理ツールのバージョンが適切に設定されていることを検証
     */
    @Test
    void shouldHaveAppropriateToolVersions() {
        try {
            String buildGradleContent = Files.readString(Paths.get(PROJECT_ROOT, "build.gradle"));
            
            // CheckStyleバージョンの確認
            assertTrue(buildGradleContent.contains("toolVersion = '10.") || 
                      buildGradleContent.contains("toolVersion = \"10."),
                "CheckStyleのバージョンが適切に設定されている必要があります");
            
            // JaCoCoバージョンの確認
            assertTrue(buildGradleContent.contains("toolVersion = \"0.8.") ||
                      buildGradleContent.contains("toolVersion = '0.8."),
                "JaCoCoのバージョンが適切に設定されている必要があります");
            
            // SpotBugsバージョンの確認
            assertTrue(buildGradleContent.contains("toolVersion = '4.") ||
                      buildGradleContent.contains("toolVersion = \"4."),
                "SpotBugsのバージョンが適切に設定されている必要があります");
            
        } catch (IOException e) {
            fail("build.gradleファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }
}