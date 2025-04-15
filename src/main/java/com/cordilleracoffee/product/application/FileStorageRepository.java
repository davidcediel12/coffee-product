package com.cordilleracoffee.product.application;

import com.cordilleracoffee.product.domain.model.TemporalImage;

import java.util.List;

public interface FileStorageRepository {
    String generateImageUploadUrl(String folder, String fileName, Integer expirationMinutes);

    void copyImages(String temp, String s, List<TemporalImage> objects);
}
