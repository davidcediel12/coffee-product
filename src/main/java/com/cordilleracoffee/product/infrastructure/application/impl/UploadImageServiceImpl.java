package com.cordilleracoffee.product.infrastructure.application.impl;

import com.cordilleracoffee.product.infrastructure.application.UploadImageService;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.SignedUrl;

import java.util.List;

public class UploadImageServiceImpl implements UploadImageService {

    @Override
    public List<SignedUrl> getSignedUrls(ImageUrlRequests urlRequests) {
        // Mock implementation for demonstration purposes
        return List.of(new SignedUrl("id-1", "https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/image1.png"));
    }
}
