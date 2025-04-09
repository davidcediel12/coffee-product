package com.cordilleracoffee.product.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ImageUrlRequest(@NotBlank @Pattern(regexp = "^[a-zA-Z0-9_\\-.]+\\.(png|jpg)$",
        message = "Incorrect file type. Only .jpg and .png are allowed") String imageName) {
}
