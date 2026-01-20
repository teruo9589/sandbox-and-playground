package com.example.app.devcontainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.jqwik.api.*;
import org.junit.jupiter.api.Tag;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DevContainer設定ファイル（devcontainer.json）のプロパティベーステスト
 * 
 * **Validates: Requirements 1.2, 1.3, 1.4, 2.2, 3.3, 6.1**
 */
@Tag("Feature: vscode-devcontainer-support, Property 1: DevContainer設定ファイルの完全性")
class DevContainerConfigPropertyTest {
    
    private static final String DEVCONTAINER_JSON_PATH = ".devcontainer/devcontainer.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * プロパティ1: DevContainer設定ファイルの完全性
     * 
     * 任意のdevcontainer.jsonファイルについて、以下の全ての必須フィールドが存在し、正しい値を持つべきです：
     * - name: 空でない文字列
     * - dockerComposeFile: 既存のdocker-compose.ymlへの相対パス
     * - service: "app"（docker-compose.ymlで定義されたサービス名）
     * - workspaceFolder: "/app"
     * - customizations.vscode.extensions: 空でない配列
     * - forwardPorts: [8080, 35729, 5432]を含む配列
     * - postCreateCommand: Gradle依存関係ダウンロードコマンドを含む文字列
     * - remoteUser: "gradle"
     */
    @Property(tries = 100)
    void devcontainerConfigShouldContainAllRequiredFields() throws IOException {
        // 実際のdevcontainer.jsonファイルを読み込む
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        
        // 必須フィールドの検証
        
        // 1. name: 空でない文字列
        assertThat(config.getName())
            .as("nameフィールドは空でない文字列である必要があります")
            .isNotNull()
            .isNotEmpty();
        
        // 2. dockerComposeFile: 既存のdocker-compose.ymlへの相対パス
        assertThat(config.getDockerComposeFile())
            .as("dockerComposeFileフィールドは../docker-compose.ymlである必要があります")
            .isEqualTo("../docker-compose.yml");
        
        // 3. service: "app"（docker-compose.ymlで定義されたサービス名）
        assertThat(config.getService())
            .as("serviceフィールドはappである必要があります")
            .isEqualTo("app");
        
        // 4. workspaceFolder: "/app"
        assertThat(config.getWorkspaceFolder())
            .as("workspaceFolderフィールドは/appである必要があります")
            .isEqualTo("/app");
        
        // 5. customizations.vscode.extensions: 空でない配列
        assertThat(config.getCustomizations())
            .as("customizationsフィールドは存在する必要があります")
            .isNotNull();
        assertThat(config.getCustomizations().getVscode())
            .as("customizations.vscodeフィールドは存在する必要があります")
            .isNotNull();
        assertThat(config.getCustomizations().getVscode().getExtensions())
            .as("customizations.vscode.extensionsフィールドは空でない配列である必要があります")
            .isNotNull()
            .isNotEmpty();
        
        // 6. forwardPorts: [8080, 35729, 5432]を含む配列
        assertThat(config.getForwardPorts())
            .as("forwardPortsフィールドは[8080, 35729, 5432]を含む必要があります")
            .isNotNull()
            .contains(8080, 35729, 5432);
        
        // 7. postCreateCommand: Gradle依存関係ダウンロードコマンドを含む文字列
        assertThat(config.getPostCreateCommand())
            .as("postCreateCommandフィールドはGradle依存関係ダウンロードコマンドを含む必要があります")
            .isNotNull()
            .contains("gradle", "dependencies");
        
        // 8. remoteUser: "gradle"
        assertThat(config.getRemoteUser())
            .as("remoteUserフィールドはgradleである必要があります")
            .isEqualTo("gradle");
    }
    
    /**
     * プロパティ1の追加検証: 必須フィールドの型チェック
     * 
     * 各フィールドが正しい型であることを検証します。
     */
    @Property(tries = 100)
    void devcontainerConfigFieldsShouldHaveCorrectTypes() throws IOException {
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        
        // 型の検証
        assertThat(config.getName()).isInstanceOf(String.class);
        assertThat(config.getDockerComposeFile()).isInstanceOf(String.class);
        assertThat(config.getService()).isInstanceOf(String.class);
        assertThat(config.getWorkspaceFolder()).isInstanceOf(String.class);
        assertThat(config.getForwardPorts()).isInstanceOf(List.class);
        assertThat(config.getPostCreateCommand()).isInstanceOf(String.class);
        assertThat(config.getRemoteUser()).isInstanceOf(String.class);
        
        // forwardPortsの各要素が整数であることを検証
        config.getForwardPorts().forEach(port -> {
            assertThat(port).isInstanceOf(Integer.class);
            assertThat(port).isPositive();
        });
    }
    
    /**
     * プロパティ1の追加検証: dockerComposeFileが実際に存在するファイルを参照しているか
     */
    @Property(tries = 100)
    void dockerComposeFileShouldExist() throws IOException {
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        
        // dockerComposeFileが参照するファイルが存在することを検証
        // .devcontainerディレクトリからの相対パスを解決
        File dockerComposeFile = new File(".devcontainer", config.getDockerComposeFile());
        assertThat(dockerComposeFile)
            .as("dockerComposeFileが参照するファイルが存在する必要があります")
            .exists()
            .isFile();
    }
    
    /**
     * プロパティ1の追加検証: 必須VS Code拡張機能が含まれているか
     * 
     * 要件4.1, 4.2, 4.3に基づいて、必須の拡張機能が含まれていることを検証します。
     */
    @Property(tries = 100)
    void extensionsShouldContainRequiredExtensions() throws IOException {
        File devcontainerFile = new File(DEVCONTAINER_JSON_PATH);
        assertThat(devcontainerFile).exists();
        
        DevContainerConfig config = objectMapper.readValue(devcontainerFile, DevContainerConfig.class);
        List<String> extensions = config.getCustomizations().getVscode().getExtensions();
        
        // Java開発用拡張機能（要件4.1）
        assertThat(extensions)
            .as("Java開発用拡張機能が含まれている必要があります")
            .contains(
                "vscjava.vscode-java-pack",
                "vscjava.vscode-java-debug",
                "vscjava.vscode-java-test"
            );
        
        // Spring Boot開発用拡張機能（要件4.2）
        assertThat(extensions)
            .as("Spring Boot開発用拡張機能が含まれている必要があります")
            .contains(
                "vmware.vscode-spring-boot",
                "vscjava.vscode-spring-initializr",
                "vscjava.vscode-spring-boot-dashboard"
            );
        
        // Docker用拡張機能（要件4.3）
        assertThat(extensions)
            .as("Docker用拡張機能が含まれている必要があります")
            .contains("ms-azuretools.vscode-docker");
    }
}
