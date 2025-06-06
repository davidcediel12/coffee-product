package com.cordilleracoffee.product.application;

import com.cordilleracoffee.product.domain.model.UserRole;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.SignedUrl;

import java.util.List;

public interface UploadImageService {
    List<SignedUrl> getSignedUrls(ImageUrlRequests urlRequests, String userId, List<UserRole> userRoles);
}
