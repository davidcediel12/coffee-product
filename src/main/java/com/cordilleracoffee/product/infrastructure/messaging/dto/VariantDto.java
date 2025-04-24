package com.cordilleracoffee.product.infrastructure.messaging.dto;

import java.util.List;

public record VariantDto(Long id, String name, String description, int stock, MonetaryAmount basePrice,
                         Boolean isPrimary, String sku, List<ImageDto> images) {
}
