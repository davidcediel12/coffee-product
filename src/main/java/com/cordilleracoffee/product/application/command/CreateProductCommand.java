package com.cordilleracoffee.product.application.command;

import com.cordilleracoffee.product.domain.model.UserRole;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.CreateProductRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateProductCommand(@NotNull @Valid CreateProductRequest request,
                                   @NotBlank String userId,
                                   @NotEmpty List<UserRole> userRoles) {
}
