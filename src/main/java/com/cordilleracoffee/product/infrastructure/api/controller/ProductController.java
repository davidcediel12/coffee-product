package com.cordilleracoffee.product.infrastructure.api.controller;


import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.SignedUrl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
class ProductController {


    private final UploadImageService uploadImageService;

    public ProductController(UploadImageService uploadImageService) {
        this.uploadImageService = uploadImageService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/images/upload-urls")
    List<SignedUrl> getUploadUrls(@RequestBody @Valid ImageUrlRequests imageUrlRequests) {
        return uploadImageService.getSignedUrls(imageUrlRequests);
    }
}
