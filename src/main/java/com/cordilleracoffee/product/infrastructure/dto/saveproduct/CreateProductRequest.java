package com.cordilleracoffee.product.infrastructure.dto.saveproduct;

import com.cordilleracoffee.product.domain.model.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
        Integer stock,

        @NotNull
        ProductStatus status,

        @Valid
        MonetaryAmount basePrice,

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
