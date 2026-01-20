package com.example.app.devcontainer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Docker Compose設定ファイル（docker-compose.yml）のモデルクラス
 */
public class DockerComposeConfig {
    
    private Map<String, Service> services;
    private Map<String, Volume> volumes;
    private Map<String, Network> networks;
    
    // Getters and Setters
    
    public Map<String, Service> getServices() {
        return services;
    }
    
    public void setServices(Map<String, Service> services) {
        this.services = services;
    }
    
    public Map<String, Volume> getVolumes() {
        return volumes;
    }
    
    public void setVolumes(Map<String, Volume> volumes) {
        this.volumes = volumes;
    }
    
    public Map<String, Network> getNetworks() {
        return networks;
    }
    
    public void setNetworks(Map<String, Network> networks) {
        this.networks = networks;
    }
    
    /**
     * サービス定義のモデルクラス
     */
    public static class Service {
        private Build build;
        private String image;
        @JsonProperty("container_name")
        private String containerName;
        private List<String> ports;
        private List<String> environment;
        private List<String> volumes;
        @JsonProperty("depends_on")
        private Object dependsOn; // Map<String, DependsOnConfig>またはList<String>
        private List<String> networks;
        private String restart;
        private HealthCheck healthcheck;
        
        // Getters and Setters
        
        public Build getBuild() {
            return build;
        }
        
        public void setBuild(Build build) {
            this.build = build;
        }
        
        public String getImage() {
            return image;
        }
        
        public void setImage(String image) {
            this.image = image;
        }
        
        public String getContainerName() {
            return containerName;
        }
        
        public void setContainerName(String containerName) {
            this.containerName = containerName;
        }
        
        public List<String> getPorts() {
            return ports;
        }
        
        public void setPorts(List<String> ports) {
            this.ports = ports;
        }
        
        public List<String> getEnvironment() {
            return environment;
        }
        
        public void setEnvironment(List<String> environment) {
            this.environment = environment;
        }
        
        public List<String> getVolumes() {
            return volumes;
        }
        
        public void setVolumes(List<String> volumes) {
            this.volumes = volumes;
        }
        
        public Object getDependsOn() {
            return dependsOn;
        }
        
        public void setDependsOn(Object dependsOn) {
            this.dependsOn = dependsOn;
        }
        
        public List<String> getNetworks() {
            return networks;
        }
        
        public void setNetworks(List<String> networks) {
            this.networks = networks;
        }
        
        public String getRestart() {
            return restart;
        }
        
        public void setRestart(String restart) {
            this.restart = restart;
        }
        
        public HealthCheck getHealthcheck() {
            return healthcheck;
        }
        
        public void setHealthcheck(HealthCheck healthcheck) {
            this.healthcheck = healthcheck;
        }
    }
    
    /**
     * ビルド設定のモデルクラス
     */
    public static class Build {
        private String context;
        private String dockerfile;
        
        // Getters and Setters
        
        public String getContext() {
            return context;
        }
        
        public void setContext(String context) {
            this.context = context;
        }
        
        public String getDockerfile() {
            return dockerfile;
        }
        
        public void setDockerfile(String dockerfile) {
            this.dockerfile = dockerfile;
        }
    }
    
    /**
     * ヘルスチェック設定のモデルクラス
     */
    public static class HealthCheck {
        private List<String> test;
        private String interval;
        private String timeout;
        private Integer retries;
        
        // Getters and Setters
        
        public List<String> getTest() {
            return test;
        }
        
        public void setTest(List<String> test) {
            this.test = test;
        }
        
        public String getInterval() {
            return interval;
        }
        
        public void setInterval(String interval) {
            this.interval = interval;
        }
        
        public String getTimeout() {
            return timeout;
        }
        
        public void setTimeout(String timeout) {
            this.timeout = timeout;
        }
        
        public Integer getRetries() {
            return retries;
        }
        
        public void setRetries(Integer retries) {
            this.retries = retries;
        }
    }
    
    /**
     * ボリューム定義のモデルクラス
     */
    public static class Volume {
        private String driver;
        
        // Getters and Setters
        
        public String getDriver() {
            return driver;
        }
        
        public void setDriver(String driver) {
            this.driver = driver;
        }
    }
    
    /**
     * ネットワーク定義のモデルクラス
     */
    public static class Network {
        private String driver;
        
        // Getters and Setters
        
        public String getDriver() {
            return driver;
        }
        
        public void setDriver(String driver) {
            this.driver = driver;
        }
    }
}
