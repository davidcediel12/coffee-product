package com.cordilleracoffee.product.application;

import com.cordilleracoffee.product.domain.model.ProductImage;

import java.util.Set;

public interface FileStorageRepository {
    String generateImageUploadUrl(String folder, String fileName, Integer expirationMinutes);

    void copyImages(String temp, String destinationFolder, Set<ProductImage> images);
}
