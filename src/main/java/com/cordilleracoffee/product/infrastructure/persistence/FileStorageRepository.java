package com.cordilleracoffee.product.infrastructure.persistence;

public interface FileStorageRepository {
    String generateImageUploadUrl(String folder, String fileName, Integer expirationMinutes);
}
