package com.cordilleracoffee.product.infrastructure.persistence;

import com.azure.storage.blob.BlobClient;

public interface BlobClientFactory {

    BlobClient createBlobClient(String containerName, String blobName);
}
