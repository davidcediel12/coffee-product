package com.cordilleracoffee.product.infrastructure.dto.saveproduct;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VariantDto(@NotBlank String name,

                         @NotBlank String description,

                         @Min(0) int stock,

                         @Valid MonetaryAmount basePrice,

                         @NotNull Boolean isPrimary,

                         @NotBlank String sku,
                         @NotEmpty @Valid List<ImageDto> images) {
}
