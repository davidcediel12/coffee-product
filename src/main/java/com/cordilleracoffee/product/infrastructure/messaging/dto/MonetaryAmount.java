package com.cordilleracoffee.product.infrastructure.messaging.dto;

import java.math.BigDecimal;

public record MonetaryAmount(BigDecimal amount, String currency) {
}
