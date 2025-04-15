package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.application.FileStorageRepository;
import com.cordilleracoffee.product.application.annotation.UseCase;
import com.cordilleracoffee.product.application.command.CreateProductCommand;
import com.cordilleracoffee.product.application.exception.InvalidProductException;
import com.cordilleracoffee.product.domain.commands.CreateProduct;
import com.cordilleracoffee.product.domain.model.*;
import com.cordilleracoffee.product.domain.repository.ImageRepository;
import com.cordilleracoffee.product.domain.repository.ProductRepository;
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
    private final FileStorageRepository fileStorageRepository;
    private final ProductRepository productRepository;

    public CreateProductServiceImpl(ProductService productService, ImageRepository imageRepository, FileStorageRepository fileStorageRepository, ProductRepository productRepository) {
        this.productService = productService;
        this.imageRepository = imageRepository;
        this.fileStorageRepository = fileStorageRepository;
        this.productRepository = productRepository;
    }

    public URI createProduct(@Valid CreateProductCommand createProductCommand) {

        productService.validateProduct(createProductCommand.request().name(), createProductCommand.request().sku());

        List<ProductImage> productImages = createProductImages(createProductCommand);

        Product product = productService.createProduct(toDomainCommand(createProductCommand, productImages));
        fileStorageRepository.copyImages("temp", "product-assets", product.getImages());

        productRepository.save(product);

        return URI.create("http://localhost:8080/products/12345");
    }

    private List<ProductImage> createProductImages(CreateProductCommand createProductCommand) {
        Map<String, TemporalImage> imageMap = imageRepository.getTemporalImages(createProductCommand.userId());

        return createProductCommand.request().images().stream()
                .map(imageDto -> {
                    boolean imageNotPresent = !imageMap.containsKey(imageDto.id().toString());
                    if (imageNotPresent) {
                        throw new InvalidProductException("There is temporal image ids that are not present in the system");
                    }
                    TemporalImage temporalImage = imageMap.get(imageDto.id().toString());

                    return new ProductImage(null, imageDto.displayOrder(), imageDto.isPrimary(), temporalImage.url());
                })
                .toList();
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
