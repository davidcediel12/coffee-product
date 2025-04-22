package com.cordilleracoffee.product.infrastructure.messaging.dto;

public record ImageDto(Long id, String name, String url, Boolean isPrimary, int displayOrder) {
}
