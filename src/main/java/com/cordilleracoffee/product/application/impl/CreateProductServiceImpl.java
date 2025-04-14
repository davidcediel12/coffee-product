package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.application.annotation.UseCase;
import com.cordilleracoffee.product.application.command.CreateProductCommand;
import com.cordilleracoffee.product.domain.commands.CreateProduct;
import com.cordilleracoffee.product.domain.model.*;
import com.cordilleracoffee.product.domain.services.ProductService;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.CreateProductRequest;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.TagDto;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@UseCase
public class CreateProductServiceImpl {

    private final ProductService productService;

    public CreateProductServiceImpl(ProductService productService) {
        this.productService = productService;
    }

    public URI createProduct(@Valid CreateProductCommand createProductCommand) {

        productService.createProduct(toDomainCommand(createProductCommand, List.of()));
        return URI.create("http://localhost:8080/products/12345");
    }


    private CreateProduct toDomainCommand(CreateProductCommand createProductCommand, List<ProductImage> images) {

        CreateProductRequest productRequest = createProductCommand.request();

        List<Variant> variants = productRequest.variants().stream()
                .map(variant -> new Variant(
                        variant.name(), variant.description(),
                        new Stock((long) variant.stock()),
                        new Money(productRequest.basePrice().amount(), productRequest.basePrice().currency()),
                        variant.isPrimary(), new Sku(variant.sku()
                )))
                .toList();


        List<Long> tagIds = productRequest.tags().stream()
                .map(TagDto::id)
                .toList();

        return new CreateProduct(
                createProductCommand.userId(), productRequest.name(),
                productRequest.description(), productRequest.category().id(),
                new Sku(productRequest.sku()), new Stock(Long.valueOf(productRequest.stock())),
                productRequest.status(),
                new Money(productRequest.basePrice().amount(), productRequest.basePrice().currency()),
                images, variants, tagIds
        );
    }
}
