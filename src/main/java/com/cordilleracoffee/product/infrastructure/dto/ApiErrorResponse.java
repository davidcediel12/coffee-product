package com.cordilleracoffee.product.infrastructure.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(LocalDateTime timestamp, String error, String message, String path) {
}
