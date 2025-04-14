package com.cordilleracoffee.product.infrastructure.dto.saveproduct;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ImageDto(
        @NotNull
        UUID id,

        @NotNull
        Boolean isPrimary,

        @Min(0)
        int displayOrder
) {
}
