package com.cordilleracoffee.product;

import com.cordilleracoffee.product.application.command.CreateProductCommand;
import jakarta.validation.Valid;

import java.net.URI;

public interface CreateProductService {
    URI createProduct(@Valid CreateProductCommand createProductCommand);
}
