package com.cordilleracoffee.product.infrastructure.application;

import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.SignedUrl;

import java.util.List;

public interface UploadImageService {
    List<SignedUrl> getSignedUrls(ImageUrlRequests urlRequests);
}
