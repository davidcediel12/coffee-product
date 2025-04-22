package com.cordilleracoffee.product.infrastructure.messaging.dto;

import java.util.List;

public record VariantDto(String name, String description, int stock, MonetaryAmount basePrice,
                         Boolean isPrimary, String sku, List<ImageDto> images) {
}
