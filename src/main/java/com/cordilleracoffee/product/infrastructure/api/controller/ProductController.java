package com.cordilleracoffee.product.infrastructure.api.controller;


import com.cordilleracoffee.product.CreateProductService;
import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.application.command.CreateProductCommand;
import com.cordilleracoffee.product.domain.model.UserRole;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.SignedUrl;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.CreateProductRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/products")
class ProductController {


    private final UploadImageService uploadImageService;
    private final CreateProductService createProductService;

    public ProductController(UploadImageService uploadImageService, CreateProductService createProductService) {
        this.uploadImageService = uploadImageService;
        this.createProductService = createProductService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/images/upload-urls")
    List<SignedUrl> generateUploadUrls(@RequestBody @Valid ImageUrlRequests imageUrlRequests,
                                       @RequestHeader("App-User-Roles") List<UserRole> userRoles,
                                       @RequestHeader("App-User-ID") String userId) {

        return uploadImageService.getSignedUrls(imageUrlRequests, userId, userRoles);
    }


    @PostMapping
    ResponseEntity<Void> createProduct(@RequestBody @Valid CreateProductRequest createProductRequest,
                                       @RequestHeader("App-User-Roles") List<UserRole> userRoles,
                                       @RequestHeader("App-User-ID") String userId) {

        CreateProductCommand command = new CreateProductCommand(createProductRequest, userId, userRoles);

        Long productId = createProductService.createProduct(command);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(productId)
                .toUri();

        return ResponseEntity.created(location)
                .build();
    }
}
