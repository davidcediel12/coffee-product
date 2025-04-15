package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.cordilleracoffee.product.application.FileStorageRepository;
import com.cordilleracoffee.product.domain.model.ProductImage;
import com.cordilleracoffee.product.infrastructure.persistence.BlobClientFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Set;

@Service
public class AzureStorageRepository implements FileStorageRepository {

    private final BlobClientFactory blobClientFactory;

    public AzureStorageRepository(BlobClientFactory blobClientFactory) {
        this.blobClientFactory = blobClientFactory;
    }

    @Override
    public String generateImageUploadUrl(String folder, String fileName, Integer expirationMinutes) {

        validateInputParameters(folder, fileName, expirationMinutes);
        BlobClient blobClient = blobClientFactory.createBlobClient(folder, fileName);

        OffsetDateTime expiryTime = OffsetDateTime.now()
                .plusMinutes(expirationMinutes);

        BlobSasPermission blobSasPermission = new BlobSasPermission()
                .setWritePermission(true);

        BlobServiceSasSignatureValues sasSignatureValues = new BlobServiceSasSignatureValues(expiryTime, blobSasPermission)
                .setStartTime(OffsetDateTime.now());

        return blobClient.getBlobUrl() + "?" + blobClient.generateSas(sasSignatureValues);
    }

    @Override
    public void copyImages(String temp, String destinationFolder, Set<ProductImage> images) {

    }

    private void validateInputParameters(String folder, String fileName, Integer expirationMinutes) {

        if (folder == null || folder.isEmpty() || fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Folder and fileName cannot be empty");
        }

        if (expirationMinutes == null || expirationMinutes < 0) {
            throw new IllegalArgumentException("Expiration minutes must be a non-null positive number");
        }

        if (expirationMinutes > 60) {
            throw new IllegalArgumentException("Expiration should be less or equal than 60 minutes");
        }
    }
}
