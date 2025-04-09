package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequest;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.SignedUrl;
import com.cordilleracoffee.product.infrastructure.persistence.FileStorageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UploadImageServiceImpl implements UploadImageService {

    private final FileStorageRepository fileStorageRepository;

    public UploadImageServiceImpl(FileStorageRepository fileStorageRepository) {
        this.fileStorageRepository = fileStorageRepository;
    }


    @Override
    public List<SignedUrl> getSignedUrls(ImageUrlRequests urlRequests) {

        List<SignedUrl> signedUrls = new ArrayList<>();

        for (ImageUrlRequest request : urlRequests.files()) {

            String blobName = UUID.randomUUID() + "_" + request.imageName();

            String uploadUrl = fileStorageRepository.generateImageUploadUrl("temp", blobName, 2);

            SignedUrl signedUrl = new SignedUrl(UUID.randomUUID().toString(), uploadUrl);
            signedUrls.add(signedUrl);
        }


        return signedUrls;
    }
}
