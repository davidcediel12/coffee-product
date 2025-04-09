package com.cordilleracoffee.product.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("spring.cloud.azure.storage.blob")
@Component
public class AzureProperties {

    private String connectionString;
    private String endpoint;
    private String containerTemp;
    private String containerProductAssets;

    public String getContainerTemp() {
        return containerTemp;
    }

    public void setContainerTemp(String containerTemp) {
        this.containerTemp = containerTemp;
    }

    public String getContainerProductAssets() {
        return containerProductAssets;
    }

    public void setContainerProductAssets(String containerProductAssets) {
        this.containerProductAssets = containerProductAssets;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }
}
