package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.cordilleracoffee.product.infrastructure.persistence.BlobClientFactory;
import org.springframework.stereotype.Service;

@Service
public class BlobClientFactoryImpl implements BlobClientFactory {

    private final BlobServiceClient blobServiceClient;

    public BlobClientFactoryImpl(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    @Override
    public BlobClient createBlobClient(String containerName, String blobName) {
        return blobServiceClient.getBlobContainerClient(containerName).getBlobClient(blobName);
    }
}
