package com.cordilleracoffee.product.infrastructure.dto.saveproduct;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MonetaryAmount(@Positive @NotNull BigDecimal amount,
                             @NotBlank String currency) {
}
