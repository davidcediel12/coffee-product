package com.cordilleracoffee.product.application;

public interface FileStorageRepository {
    String generateImageUploadUrl(String folder, String fileName, Integer expirationMinutes);

    String changeImageLocation(String source, String destination, String imageName, String finalImageName);
}
