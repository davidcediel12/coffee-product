package com.cordilleracoffee.product.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ImageUrlRequests(@Valid
                               @NotEmpty(message = "At least one file is required")
                               @Size(max = 10, message = "At most 10 images are allowed")
                               List<ImageUrlRequest> files) {
}
