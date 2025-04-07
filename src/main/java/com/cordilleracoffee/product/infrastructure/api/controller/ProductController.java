package com.cordilleracoffee.product.infrastructure.api.controller;


import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.SignedUrl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
class ProductController {




    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/images/upload-urls")
    List<SignedUrl> getUploadUrls(@RequestBody @Valid ImageUrlRequests imageUrlRequests) {
        return List.of(
                new SignedUrl("25", "https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/test1.jpg"),
                new SignedUrl("26", "https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/test2.jpg")
        );
    }
}
