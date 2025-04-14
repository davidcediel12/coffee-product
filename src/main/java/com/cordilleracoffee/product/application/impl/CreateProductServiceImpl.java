package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.application.command.CreateProductCommand;

import java.net.URI;

public class CreateProductServiceImpl {


    public URI createProduct(CreateProductCommand createProductCommand){
        return URI.create("http://localhost:8080/products/12345");
    }
}
