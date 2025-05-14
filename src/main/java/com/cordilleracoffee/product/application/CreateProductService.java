package com.cordilleracoffee.product.application;

import com.cordilleracoffee.product.application.command.CreateProductCommand;
import jakarta.validation.Valid;

public interface CreateProductService {
    Long createProduct(@Valid CreateProductCommand createProductCommand);
}
