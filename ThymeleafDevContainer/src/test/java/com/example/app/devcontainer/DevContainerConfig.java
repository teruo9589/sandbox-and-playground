package com.example.app.devcontainer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * DevContainer設定ファイル（devcontainer.json）のモデルクラス
 */
public class DevContainerConfig {
    
    private String name;
    private String dockerComposeFile;
    private String service;
    private String workspaceFolder;
    private Customizations customizations;
    private List<Integer> forwardPorts;
    private String postCreateCommand;
    private String remoteUser;
    
    // Getters and Setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDockerComposeFile() {
        return dockerComposeFile;
    }
    
    public void setDockerComposeFile(String dockerComposeFile) {
        this.dockerComposeFile = dockerComposeFile;
    }
    
    public String getService() {
        return service;
    }
    
    public void setService(String service) {
        this.service = service;
    }
    
    public String getWorkspaceFolder() {
        return workspaceFolder;
    }
    
    public void setWorkspaceFolder(String workspaceFolder) {
        this.workspaceFolder = workspaceFolder;
    }
    
    public Customizations getCustomizations() {
        return customizations;
    }
    
    public void setCustomizations(Customizations customizations) {
        this.customizations = customizations;
    }
    
    public List<Integer> getForwardPorts() {
        return forwardPorts;
    }
    
    public void setForwardPorts(List<Integer> forwardPorts) {
        this.forwardPorts = forwardPorts;
    }
    
    public String getPostCreateCommand() {
        return postCreateCommand;
    }
    
    public void setPostCreateCommand(String postCreateCommand) {
        this.postCreateCommand = postCreateCommand;
    }
    
    public String getRemoteUser() {
        return remoteUser;
    }
    
    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }
    
    /**
     * customizationsセクションのモデルクラス
     */
    public static class Customizations {
        private VsCode vscode;
        
        public VsCode getVscode() {
            return vscode;
        }
        
        public void setVscode(VsCode vscode) {
            this.vscode = vscode;
        }
    }
    
    /**
     * vscodeセクションのモデルクラス
     */
    public static class VsCode {
        private List<String> extensions;
        private Map<String, Object> settings;
        
        public List<String> getExtensions() {
            return extensions;
        }
        
        public void setExtensions(List<String> extensions) {
            this.extensions = extensions;
        }
        
        public Map<String, Object> getSettings() {
            return settings;
        }
        
        public void setSettings(Map<String, Object> settings) {
            this.settings = settings;
        }
    }
}
