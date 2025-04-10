package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.application.FileStorageRepository;
import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.application.annotation.UseCase;
import com.cordilleracoffee.product.application.exception.UnauthorizedUserException;
import com.cordilleracoffee.product.domain.model.UserRole;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequest;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.SignedUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UseCase
public class UploadImageServiceImpl implements UploadImageService {

    public static final String TEMP_FOLDER = "temp";
    private final FileStorageRepository fileStorageRepository;

    public UploadImageServiceImpl(FileStorageRepository fileStorageRepository) {
        this.fileStorageRepository = fileStorageRepository;
    }


    @Override
    public List<SignedUrl> getSignedUrls(ImageUrlRequests urlRequests, List<UserRole> userRoles) {

        if (!userRoles.contains(UserRole.SELLER)) {
            throw new UnauthorizedUserException("User must be seller to request url to upload images");
        }

        List<SignedUrl> signedUrls = new ArrayList<>();

        for (ImageUrlRequest request : urlRequests.files()) {

            String blobName = UUID.randomUUID() + "_" + request.imageName();

            String uploadUrl = fileStorageRepository.generateImageUploadUrl(TEMP_FOLDER, blobName, 2);

            SignedUrl signedUrl = new SignedUrl(UUID.randomUUID().toString(), uploadUrl);
            signedUrls.add(signedUrl);
        }


        return signedUrls;
    }
}
