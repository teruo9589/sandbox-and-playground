package com.example.app.devcontainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.jqwik.api.*;
import org.junit.jupiter.api.Tag;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Docker Compose設定ファイル（docker-compose.yml）のプロパティベーステスト
 * 
 * **Validates: Requirements 2.1, 2.3, 3.1**
 */
@Tag("Feature: vscode-devcontainer-support, Property 3: Docker Compose設定の整合性")
class DockerComposeConfigPropertyTest {
    
    private static final String DOCKER_COMPOSE_PATH = "docker-compose.yml";
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    
    /**
     * プロパティ3: Docker Compose設定の整合性
     * 
     * 任意のdocker-compose.ymlファイルについて、appサービスは以下の全ての設定を含むべきです：
     * - build.dockerfile: "Dockerfile.dev"を参照
     * - ports: 8080:8080と35729:35729のマッピングを含む
     * - environment: SPRING_PROFILES_ACTIVEとDB_PASSWORDを含む
     * - volumes: ./src:/app/srcマッピングとgradle-cacheボリュームを含む
     * - depends_on: postgresサービスへの依存を含む
     * - networks: app-networkへの接続を含む
     */
    @Property(tries = 100)
    void appServiceShouldContainAllRequiredSettings() throws IOException {
        // 実際のdocker-compose.ymlファイルを読み込む
        File dockerComposeFile = new File(DOCKER_COMPOSE_PATH);
        assertThat(dockerComposeFile)
            .as("docker-compose.ymlファイルが存在する必要があります")
            .exists();
        
        DockerComposeConfig config = yamlMapper.readValue(dockerComposeFile, DockerComposeConfig.class);
        
        // servicesセクションが存在することを確認
        assertThat(config.getServices())
            .as("servicesセクションが存在する必要があります")
            .isNotNull()
            .isNotEmpty();
        
        // appサービスが存在することを確認
        assertThat(config.getServices())
            .as("appサービスが定義されている必要があります")
            .containsKey("app");
        
        DockerComposeConfig.Service appService = config.getServices().get("app");
        
        // 1. build.dockerfile: "Dockerfile.dev"を参照
        assertThat(appService.getBuild())
            .as("appサービスにbuild設定が存在する必要があります")
            .isNotNull();
        assertThat(appService.getBuild().getDockerfile())
            .as("build.dockerfileはDockerfile.devである必要があります")
            .isEqualTo("Dockerfile.dev");
        
        // 2. ports: 8080:8080と35729:35729のマッピングを含む
        assertThat(appService.getPorts())
            .as("appサービスにports設定が存在する必要があります")
            .isNotNull()
            .isNotEmpty();
        assertThat(appService.getPorts())
            .as("portsは8080:8080と35729:35729のマッピングを含む必要があります")
            .contains("8080:8080", "35729:35729");
        
        // 3. environment: SPRING_PROFILES_ACTIVEとDB_PASSWORDを含む
        assertThat(appService.getEnvironment())
            .as("appサービスにenvironment設定が存在する必要があります")
            .isNotNull()
            .isNotEmpty();
        
        // 環境変数の検証（SPRING_PROFILES_ACTIVE）
        boolean hasSpringProfilesActive = appService.getEnvironment().stream()
            .anyMatch(env -> env.startsWith("SPRING_PROFILES_ACTIVE="));
        assertThat(hasSpringProfilesActive)
            .as("environmentはSPRING_PROFILES_ACTIVEを含む必要があります")
            .isTrue();
        
        // 環境変数の検証（DB_PASSWORD）
        boolean hasDbPassword = appService.getEnvironment().stream()
            .anyMatch(env -> env.startsWith("DB_PASSWORD="));
        assertThat(hasDbPassword)
            .as("environmentはDB_PASSWORDを含む必要があります")
            .isTrue();
        
        // 4. volumes: ./src:/app/srcマッピングとgradle-cacheボリュームを含む
        assertThat(appService.getVolumes())
            .as("appサービスにvolumes設定が存在する必要があります")
            .isNotNull()
            .isNotEmpty();
        
        // ソースコードマッピングの検証
        boolean hasSrcMapping = appService.getVolumes().stream()
            .anyMatch(vol -> vol.contains("./src") && vol.contains("/app/src"));
        assertThat(hasSrcMapping)
            .as("volumesは./src:/app/srcマッピングを含む必要があります")
            .isTrue();
        
        // Gradleキャッシュボリュームの検証
        boolean hasGradleCache = appService.getVolumes().stream()
            .anyMatch(vol -> vol.contains("gradle-cache"));
        assertThat(hasGradleCache)
            .as("volumesはgradle-cacheボリュームを含む必要があります")
            .isTrue();
        
        // 5. depends_on: postgresサービスへの依存を含む
        assertThat(appService.getDependsOn())
            .as("appサービスにdepends_on設定が存在する必要があります")
            .isNotNull();
        
        // depends_onがMapの場合（詳細設定）
        if (appService.getDependsOn() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dependsOnMap = (Map<String, Object>) appService.getDependsOn();
            assertThat(dependsOnMap)
                .as("depends_onはpostgresサービスへの依存を含む必要があります")
                .containsKey("postgres");
        }
        // depends_onがListの場合（シンプル設定）
        else if (appService.getDependsOn() instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> dependsOnList = (List<String>) appService.getDependsOn();
            assertThat(dependsOnList)
                .as("depends_onはpostgresサービスへの依存を含む必要があります")
                .contains("postgres");
        }
        
        // 6. networks: app-networkへの接続を含む
        assertThat(appService.getNetworks())
            .as("appサービスにnetworks設定が存在する必要があります")
            .isNotNull()
            .isNotEmpty();
        assertThat(appService.getNetworks())
            .as("networksはapp-networkへの接続を含む必要があります")
            .contains("app-network");
    }
    
    /**
     * プロパティ3の追加検証: build.contextが正しく設定されているか
     */
    @Property(tries = 100)
    void appServiceBuildContextShouldBeValid() throws IOException {
        File dockerComposeFile = new File(DOCKER_COMPOSE_PATH);
        assertThat(dockerComposeFile).exists();
        
        DockerComposeConfig config = yamlMapper.readValue(dockerComposeFile, DockerComposeConfig.class);
        DockerComposeConfig.Service appService = config.getServices().get("app");
        
        // build.contextが存在し、有効な値であることを検証
        assertThat(appService.getBuild().getContext())
            .as("build.contextが存在する必要があります")
            .isNotNull()
            .isNotEmpty();
        
        // contextが"."（カレントディレクトリ）であることを検証
        assertThat(appService.getBuild().getContext())
            .as("build.contextは.（カレントディレクトリ）である必要があります")
            .isEqualTo(".");
    }
    
    /**
     * プロパティ3の追加検証: ポート番号が有効な範囲内であるか
     */
    @Property(tries = 100)
    void appServicePortsShouldBeValid() throws IOException {
        File dockerComposeFile = new File(DOCKER_COMPOSE_PATH);
        assertThat(dockerComposeFile).exists();
        
        DockerComposeConfig config = yamlMapper.readValue(dockerComposeFile, DockerComposeConfig.class);
        DockerComposeConfig.Service appService = config.getServices().get("app");
        
        // 各ポートマッピングが有効な形式であることを検証
        appService.getPorts().forEach(portMapping -> {
            assertThat(portMapping)
                .as("ポートマッピングは'ホストポート:コンテナポート'の形式である必要があります")
                .contains(":");
            
            String[] ports = portMapping.split(":");
            assertThat(ports)
                .as("ポートマッピングは2つの部分から構成される必要があります")
                .hasSize(2);
            
            // ポート番号が数値であることを検証
            int hostPort = Integer.parseInt(ports[0]);
            int containerPort = Integer.parseInt(ports[1]);
            
            // ポート番号が有効な範囲内であることを検証（1-65535）
            assertThat(hostPort)
                .as("ホストポート番号は1-65535の範囲内である必要があります")
                .isBetween(1, 65535);
            assertThat(containerPort)
                .as("コンテナポート番号は1-65535の範囲内である必要があります")
                .isBetween(1, 65535);
        });
    }
    
    /**
     * プロパティ3の追加検証: 環境変数の値が適切に設定されているか
     */
    @Property(tries = 100)
    void appServiceEnvironmentVariablesShouldHaveValues() throws IOException {
        File dockerComposeFile = new File(DOCKER_COMPOSE_PATH);
        assertThat(dockerComposeFile).exists();
        
        DockerComposeConfig config = yamlMapper.readValue(dockerComposeFile, DockerComposeConfig.class);
        DockerComposeConfig.Service appService = config.getServices().get("app");
        
        // 各環境変数が"KEY=VALUE"の形式であることを検証
        appService.getEnvironment().forEach(env -> {
            assertThat(env)
                .as("環境変数は'KEY=VALUE'の形式である必要があります")
                .contains("=");
            
            String[] parts = env.split("=", 2);
            assertThat(parts)
                .as("環境変数は2つの部分から構成される必要があります")
                .hasSize(2);
            
            String key = parts[0];
            String value = parts[1];
            
            // キーが空でないことを検証
            assertThat(key)
                .as("環境変数のキーは空でない必要があります")
                .isNotEmpty();
            
            // 値が空でないことを検証
            assertThat(value)
                .as("環境変数の値は空でない必要があります")
                .isNotEmpty();
        });
    }
    
    /**
     * プロパティ3の追加検証: ボリュームマッピングが正しい形式であるか
     */
    @Property(tries = 100)
    void appServiceVolumesShouldHaveValidFormat() throws IOException {
        File dockerComposeFile = new File(DOCKER_COMPOSE_PATH);
        assertThat(dockerComposeFile).exists();
        
        DockerComposeConfig config = yamlMapper.readValue(dockerComposeFile, DockerComposeConfig.class);
        DockerComposeConfig.Service appService = config.getServices().get("app");
        
        // 各ボリュームマッピングが有効な形式であることを検証
        appService.getVolumes().forEach(volume -> {
            assertThat(volume)
                .as("ボリュームマッピングは':'を含む必要があります")
                .contains(":");
            
            String[] parts = volume.split(":");
            assertThat(parts.length)
                .as("ボリュームマッピングは2つまたは3つの部分から構成される必要があります")
                .isGreaterThanOrEqualTo(2)
                .isLessThanOrEqualTo(3);
            
            String source = parts[0];
            String target = parts[1];
            
            // ソースとターゲットが空でないことを検証
            assertThat(source)
                .as("ボリュームのソースは空でない必要があります")
                .isNotEmpty();
            assertThat(target)
                .as("ボリュームのターゲットは空でない必要があります")
                .isNotEmpty();
        });
    }
    
    /**
     * プロパティ3の追加検証: 必須のボリュームとネットワークが定義されているか
     */
    @Property(tries = 100)
    void dockerComposeShouldDefineRequiredVolumesAndNetworks() throws IOException {
        File dockerComposeFile = new File(DOCKER_COMPOSE_PATH);
        assertThat(dockerComposeFile).exists();
        
        DockerComposeConfig config = yamlMapper.readValue(dockerComposeFile, DockerComposeConfig.class);
        
        // volumesセクションが存在し、gradle-cacheとpostgres-dataを含むことを検証
        assertThat(config.getVolumes())
            .as("volumesセクションが存在する必要があります")
            .isNotNull()
            .isNotEmpty();
        assertThat(config.getVolumes())
            .as("volumesはgradle-cacheを定義する必要があります")
            .containsKey("gradle-cache");
        assertThat(config.getVolumes())
            .as("volumesはpostgres-dataを定義する必要があります")
            .containsKey("postgres-data");
        
        // networksセクションが存在し、app-networkを含むことを検証
        assertThat(config.getNetworks())
            .as("networksセクションが存在する必要があります")
            .isNotNull()
            .isNotEmpty();
        assertThat(config.getNetworks())
            .as("networksはapp-networkを定義する必要があります")
            .containsKey("app-network");
    }
}
