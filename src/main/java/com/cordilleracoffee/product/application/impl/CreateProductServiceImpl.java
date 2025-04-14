package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.application.annotation.UseCase;
import com.cordilleracoffee.product.application.command.CreateProductCommand;
import com.cordilleracoffee.product.application.exception.InvalidProductException;
import com.cordilleracoffee.product.domain.commands.CreateProduct;
import com.cordilleracoffee.product.domain.model.*;
import com.cordilleracoffee.product.domain.repository.ImageRepository;
import com.cordilleracoffee.product.domain.services.ProductService;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.CreateProductRequest;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.TagDto;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Map;

@UseCase
public class CreateProductServiceImpl {

    private final ProductService productService;
    private final ImageRepository imageRepository;

    public CreateProductServiceImpl(ProductService productService, ImageRepository imageRepository) {
        this.productService = productService;
        this.imageRepository = imageRepository;
    }

    public URI createProduct(@Valid CreateProductCommand createProductCommand) {

        createProductImages(createProductCommand);

        productService.createProduct(toDomainCommand(createProductCommand, List.of()));
        return URI.create("http://localhost:8080/products/12345");
    }

    private void createProductImages(CreateProductCommand createProductCommand) {
        Map<String, TemporalImage> imageMap = imageRepository.getTemporalImages(createProductCommand.userId());

        boolean tempImageExist = createProductCommand.request().images().stream()
                        .allMatch(image -> imageMap.containsKey(image.id().toString()));

        if(!tempImageExist) {
            throw new InvalidProductException("There is temporal image ids that are not present in the system");
        }
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
