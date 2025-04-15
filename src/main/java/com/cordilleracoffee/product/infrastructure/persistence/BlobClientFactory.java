package com.cordilleracoffee.product.infrastructure.persistence;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;

public interface BlobClientFactory {

    BlobClient createBlobClient(String containerName, String blobName);
    BlobContainerClient createBlobContainerClient(String containerName);
}
