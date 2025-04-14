package com.cordilleracoffee.product.infrastructure.dto.saveproduct;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CategoryDto(@NotNull @Positive Long id) {
}
