package com.cordilleracoffee.product.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureStorageConfig {

    @Bean
    public BlobServiceClient blobServiceClient(AzureProperties azureProperties) {
        return new BlobServiceClientBuilder()
                .endpoint(azureProperties.getEndpoint())
                .connectionString(azureProperties.getConnectionString())
                .buildClient();
    }
}
