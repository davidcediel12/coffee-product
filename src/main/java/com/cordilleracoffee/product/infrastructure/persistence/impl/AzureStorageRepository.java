package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.cordilleracoffee.product.application.FileStorageRepository;
import com.cordilleracoffee.product.infrastructure.persistence.BlobClientFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

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

        var sasSignatureValues = generateSasPermissions(expiryTime, false, true);

        return blobClient.getBlobUrl() + "?" + blobClient.generateSas(sasSignatureValues);
    }


    @Override
    public String changeImageLocation(String source, String destination, String imageName, String finalImageName) {

        BlobContainerClient sourceContainer = blobClientFactory.createBlobContainerClient(source);
        BlobContainerClient targetContainer = blobClientFactory.createBlobContainerClient(destination);

        BlobClient sourceBlobClient = sourceContainer.getBlobClient(imageName);

        var signatureValues = generateSasPermissions(OffsetDateTime.now().plusMinutes(1),
                true, true);
        String sasToken = sourceBlobClient.generateSas(signatureValues);
        String sourceUrl = sourceBlobClient.getBlobUrl() + "?"  + sasToken;

        BlobClient destinationBlob = targetContainer.getBlobClient(finalImageName);

        destinationBlob.getBlockBlobClient().uploadFromUrl(sourceUrl, true);
        sourceBlobClient.delete();

        return destinationBlob.getBlobUrl();
    }

    private static BlobServiceSasSignatureValues generateSasPermissions(
            OffsetDateTime expiryTime, boolean readPermission, boolean writePermission) {

        BlobSasPermission blobSasPermission = new BlobSasPermission()
                .setWritePermission(writePermission)
                .setReadPermission(readPermission);

        return new BlobServiceSasSignatureValues(expiryTime, blobSasPermission)
                .setStartTime(OffsetDateTime.now());
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
