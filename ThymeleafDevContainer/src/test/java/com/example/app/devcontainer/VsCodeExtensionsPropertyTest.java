package com.example.app.devcontainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.jqwik.api.*;
import org.junit.jupiter.api.Tag;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * VS Code拡張機能の完全性を検証するプロパティベーステスト
 * 
 * **Validates: Requirements 4.1, 4.2, 4.3**
 */
@Tag("Feature: vscode-devcontainer-support, Property 2: VS Code拡張機能の完全性")
class VsCodeExtensionsPropertyTest {
    
    private static final String DEVCONTAINER_JSON_PATH = ".devcontainer/devcontainer.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // Java開発用拡張機能（要件4.1）
    private static final List<String> JAVA_EXTENSIONS = Arrays.asList(
        "vscjava.vscode-java-pack",
        "vscjava.vscode-java-debug",
        "vscjava.vscode-java-test",
        "vscjava.vscode-maven",
        "vscjava.vscode-gradle"
    );
    
    // Spring Boot開発用拡張機能（要件4.2）
    private static final List<String> SPRING_BOOT_EXTENSIONS = Arrays.asList(
        "vmware.vscode-spring-boot",
        "vscjava.vscode-spring-initializr",
        "vscjava.vscode-spring-boot-dashboard"
    );
    
    // フロントエンド開発用拡張機能（要件4.1）
    private static final List<String> FRONTEND_EXTENSIONS = Arrays.asList(
        "ecmel.vscode-html-css",
        "dbaeumer.vscode-eslint",
        "esbenp.prettier-vscode",
        "formulahendry.auto-rename-tag",
        "zignd.html-css-class-completion",
        "xabikos.JavaScriptSnippets"
    );
    
    // 開発者体験向上用拡張機能（要件4.1）
    private static final List<String> DEVELOPER_EXPERIENCE_EXTENSIONS = Arrays.asList(
        "streetsidesoftware.code-spell-checker",
        "usernamehw.errorlens",
        "christian-kohler.path-intellisense"
    );
    
    // Docker用拡張機能（要件4.3）
    private static final List<String> DOCKER_EXTENSIONS = Arrays.asList(
        "ms-azuretools.vscode-docker"
    );
    
    // コード品質用拡張機能（要件4.3）
    private static final List<String> CODE_QUALITY_EXTENSIONS = Arrays.asList(
        "sonarsource.sonarlint-vscode",
        "shengchen.vscode-checkstyle"
    );
    
    /**
     * プロパティ2: VS Code拡張機能の完全性
     * 
     * 任意のdevcontainer.jsonファイルについて、customizations.vscode.extensions配列は
     * 以下の全てのカテゴリの拡張機能を含むべきです：
     * - Java開発
     * - Spring Boot開発
     * - フロントエンド開発
     * - 開発者体験向上
     * - Docker
     * - コード品質
     */
    @Property(tries = 100)
    void extensionsShouldContainAllRequiredCategories() throws IOException {
        // 実際のdevcontainer.jsonファイルを読み込む
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile)
            .as("devcontainer.jsonファイルが存在する必要があります")
            .exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        
        // customizations.vscode.extensionsが存在することを確認
        assertThat(config.getCustomizations())
            .as("customizationsフィールドが存在する必要があります")
            .isNotNull();
        assertThat(config.getCustomizations().getVscode())
            .as("customizations.vscodeフィールドが存在する必要があります")
            .isNotNull();
        
        List<String> extensions = config.getCustomizations().getVscode().getExtensions();
        assertThat(extensions)
            .as("customizations.vscode.extensionsフィールドが存在する必要があります")
            .isNotNull()
            .isNotEmpty();
        
        // 各カテゴリの拡張機能が含まれていることを検証
        
        // Java開発用拡張機能（要件4.1）
        assertThat(extensions)
            .as("Java開発用拡張機能が全て含まれている必要があります")
            .containsAll(JAVA_EXTENSIONS);
        
        // Spring Boot開発用拡張機能（要件4.2）
        assertThat(extensions)
            .as("Spring Boot開発用拡張機能が全て含まれている必要があります")
            .containsAll(SPRING_BOOT_EXTENSIONS);
        
        // フロントエンド開発用拡張機能（要件4.1）
        assertThat(extensions)
            .as("フロントエンド開発用拡張機能が全て含まれている必要があります")
            .containsAll(FRONTEND_EXTENSIONS);
        
        // 開発者体験向上用拡張機能（要件4.1）
        assertThat(extensions)
            .as("開発者体験向上用拡張機能が全て含まれている必要があります")
            .containsAll(DEVELOPER_EXPERIENCE_EXTENSIONS);
        
        // Docker用拡張機能（要件4.3）
        assertThat(extensions)
            .as("Docker用拡張機能が全て含まれている必要があります")
            .containsAll(DOCKER_EXTENSIONS);
        
        // コード品質用拡張機能（要件4.3）
        assertThat(extensions)
            .as("コード品質用拡張機能が全て含まれている必要があります")
            .containsAll(CODE_QUALITY_EXTENSIONS);
    }
    
    /**
     * プロパティ2の追加検証: 拡張機能IDの形式チェック
     * 
     * 全ての拡張機能IDが正しい形式（publisher.extension-name）であることを検証します。
     */
    @Property(tries = 100)
    void extensionIdsShouldHaveCorrectFormat() throws IOException {
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        List<String> extensions = config.getCustomizations().getVscode().getExtensions();
        
        // 各拡張機能IDが正しい形式であることを検証
        extensions.forEach(extensionId -> {
            assertThat(extensionId)
                .as("拡張機能ID '%s' は空でない必要があります", extensionId)
                .isNotNull()
                .isNotEmpty();
            
            assertThat(extensionId)
                .as("拡張機能ID '%s' は 'publisher.extension-name' の形式である必要があります", extensionId)
                .matches("^[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$");
        });
    }
    
    /**
     * プロパティ2の追加検証: 重複する拡張機能がないことを確認
     * 
     * 拡張機能リストに重複がないことを検証します。
     */
    @Property(tries = 100)
    void extensionsShouldNotContainDuplicates() throws IOException {
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        List<String> extensions = config.getCustomizations().getVscode().getExtensions();
        
        // 重複がないことを検証
        assertThat(extensions)
            .as("拡張機能リストに重複があってはいけません")
            .doesNotHaveDuplicates();
    }
    
    /**
     * プロパティ2の追加検証: Java開発用拡張機能の完全性
     * 
     * Java開発に必要な全ての拡張機能が含まれていることを個別に検証します。
     */
    @Property(tries = 100)
    void shouldContainAllJavaExtensions() throws IOException {
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        List<String> extensions = config.getCustomizations().getVscode().getExtensions();
        
        // Java開発用拡張機能を個別に検証
        assertThat(extensions)
            .as("Java Extension Pack が含まれている必要があります")
            .contains("vscjava.vscode-java-pack");
        
        assertThat(extensions)
            .as("Java Debug が含まれている必要があります")
            .contains("vscjava.vscode-java-debug");
        
        assertThat(extensions)
            .as("Java Test Runner が含まれている必要があります")
            .contains("vscjava.vscode-java-test");
        
        assertThat(extensions)
            .as("Maven for Java が含まれている必要があります")
            .contains("vscjava.vscode-maven");
        
        assertThat(extensions)
            .as("Gradle for Java が含まれている必要があります")
            .contains("vscjava.vscode-gradle");
    }
    
    /**
     * プロパティ2の追加検証: Spring Boot開発用拡張機能の完全性
     * 
     * Spring Boot開発に必要な全ての拡張機能が含まれていることを個別に検証します。
     */
    @Property(tries = 100)
    void shouldContainAllSpringBootExtensions() throws IOException {
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        List<String> extensions = config.getCustomizations().getVscode().getExtensions();
        
        // Spring Boot開発用拡張機能を個別に検証
        assertThat(extensions)
            .as("Spring Boot Extension Pack が含まれている必要があります")
            .contains("vmware.vscode-spring-boot");
        
        assertThat(extensions)
            .as("Spring Initializr が含まれている必要があります")
            .contains("vscjava.vscode-spring-initializr");
        
        assertThat(extensions)
            .as("Spring Boot Dashboard が含まれている必要があります")
            .contains("vscjava.vscode-spring-boot-dashboard");
    }
    
    /**
     * プロパティ2の追加検証: フロントエンド開発用拡張機能の完全性
     * 
     * フロントエンド開発に必要な全ての拡張機能が含まれていることを個別に検証します。
     */
    @Property(tries = 100)
    void shouldContainAllFrontendExtensions() throws IOException {
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        List<String> extensions = config.getCustomizations().getVscode().getExtensions();
        
        // フロントエンド開発用拡張機能を個別に検証
        assertThat(extensions)
            .as("HTML CSS Support が含まれている必要があります")
            .contains("ecmel.vscode-html-css");
        
        assertThat(extensions)
            .as("ESLint が含まれている必要があります")
            .contains("dbaeumer.vscode-eslint");
        
        assertThat(extensions)
            .as("Prettier が含まれている必要があります")
            .contains("esbenp.prettier-vscode");
        
        assertThat(extensions)
            .as("Auto Rename Tag が含まれている必要があります")
            .contains("formulahendry.auto-rename-tag");
        
        assertThat(extensions)
            .as("IntelliSense for CSS class names が含まれている必要があります")
            .contains("zignd.html-css-class-completion");
        
        assertThat(extensions)
            .as("JavaScript (ES6) code snippets が含まれている必要があります")
            .contains("xabikos.JavaScriptSnippets");
    }
    
    /**
     * プロパティ2の追加検証: 開発者体験向上用拡張機能の完全性
     * 
     * 開発者体験向上に必要な全ての拡張機能が含まれていることを個別に検証します。
     */
    @Property(tries = 100)
    void shouldContainAllDeveloperExperienceExtensions() throws IOException {
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        List<String> extensions = config.getCustomizations().getVscode().getExtensions();
        
        // 開発者体験向上用拡張機能を個別に検証
        assertThat(extensions)
            .as("Code Spell Checker が含まれている必要があります")
            .contains("streetsidesoftware.code-spell-checker");
        
        assertThat(extensions)
            .as("Error Lens が含まれている必要があります")
            .contains("usernamehw.errorlens");
        
        assertThat(extensions)
            .as("Path Intellisense が含まれている必要があります")
            .contains("christian-kohler.path-intellisense");
    }
    
    /**
     * プロパティ2の追加検証: Docker用拡張機能の完全性
     * 
     * Docker開発に必要な全ての拡張機能が含まれていることを個別に検証します。
     */
    @Property(tries = 100)
    void shouldContainAllDockerExtensions() throws IOException {
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        List<String> extensions = config.getCustomizations().getVscode().getExtensions();
        
        // Docker用拡張機能を個別に検証
        assertThat(extensions)
            .as("Docker が含まれている必要があります")
            .contains("ms-azuretools.vscode-docker");
    }
    
    /**
     * プロパティ2の追加検証: コード品質用拡張機能の完全性
     * 
     * コード品質管理に必要な全ての拡張機能が含まれていることを個別に検証します。
     */
    @Property(tries = 100)
    void shouldContainAllCodeQualityExtensions() throws IOException {
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        List<String> extensions = config.getCustomizations().getVscode().getExtensions();
        
        // コード品質用拡張機能を個別に検証
        assertThat(extensions)
            .as("SonarLint が含まれている必要があります")
            .contains("sonarsource.sonarlint-vscode");
        
        assertThat(extensions)
            .as("Checkstyle for Java が含まれている必要があります")
            .contains("shengchen.vscode-checkstyle");
    }
}
