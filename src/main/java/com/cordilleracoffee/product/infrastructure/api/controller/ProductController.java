package com.cordilleracoffee.product.infrastructure.api.controller;


import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.domain.model.UserRole;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.SignedUrl;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.CreateProductRequest;
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
    List<SignedUrl> generateUploadUrls(@RequestBody @Valid ImageUrlRequests imageUrlRequests,
                                       @RequestHeader("App-User-Roles") List<UserRole> userRoles,
                                       @RequestHeader("App-User-ID") String userId) {

        return uploadImageService.getSignedUrls(imageUrlRequests, userId, userRoles);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void createProduct(@RequestBody @Valid CreateProductRequest createProductRequest){
        // temp
    }
}
