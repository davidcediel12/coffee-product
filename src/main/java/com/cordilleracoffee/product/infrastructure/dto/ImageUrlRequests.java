package com.cordilleracoffee.product.infrastructure.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ImageUrlRequests(@NotEmpty(message = "At least one file is required") List<ImageUrlRequest> files) {
}
