package com.cordilleracoffee.product.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ImageUrlRequests(@Valid  @NotEmpty(message = "At least one file is required") List<ImageUrlRequest> files) {
}
