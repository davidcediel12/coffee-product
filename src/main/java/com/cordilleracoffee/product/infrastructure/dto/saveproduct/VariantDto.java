package com.cordilleracoffee.product.infrastructure.dto.saveproduct;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record VariantDto(@NotBlank
                         String name,

                         @NotBlank
                         String description,

                         @Min(0)
                         int stock,

                         @NotNull
                         @DecimalMin(value = "0.0", inclusive = false)
                         BigDecimal basePrice,

                         @NotNull
                         Boolean isPrimary,

                         @NotBlank
                         String sku) {
}
