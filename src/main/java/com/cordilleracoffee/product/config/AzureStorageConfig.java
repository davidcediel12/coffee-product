package com.cordilleracoffee.product.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("!test")
public class AzureStorageConfig {

    @Bean
    public BlobServiceClient blobServiceClient(AzureProperties azureProperties) {
        return new BlobServiceClientBuilder()
                .endpoint(azureProperties.getEndpoint())
                .connectionString(azureProperties.getConnectionString())
                .buildClient();
    }


    @Bean
    public ApplicationRunner initializeContainers(BlobServiceClient blobServiceClient, AzureProperties azureProperties) {


        return args -> {

            List<String> containers = List.of(azureProperties.getContainerTemp(), azureProperties.getContainerProductAssets());
            containers.forEach(blobServiceClient::createBlobContainerIfNotExists);
        };
    }
}
