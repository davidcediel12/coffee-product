package com.cordilleracoffee.product.infrastructure.dto.saveproduct;

import com.cordilleracoffee.product.domain.model.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        @Valid
        CategoryDto category,

        @NotBlank
        String sku,

        @Min(0)
        int stock,

        @NotNull
        ProductStatus status,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal basePrice,

        @NotNull
        @Size(min = 1)
        @Valid
        List<ImageDto> images,

        @Valid
        List<VariantDto> variants,

        @NotNull
        @Valid List<TagDto> tags
) {
}
