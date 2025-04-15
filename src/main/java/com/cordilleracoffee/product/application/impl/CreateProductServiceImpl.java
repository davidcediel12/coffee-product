package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.application.FileStorageRepository;
import com.cordilleracoffee.product.application.annotation.UseCase;
import com.cordilleracoffee.product.application.command.CreateProductCommand;
import com.cordilleracoffee.product.domain.commands.CreateProduct;
import com.cordilleracoffee.product.domain.exception.InvalidProductException;
import com.cordilleracoffee.product.domain.model.*;
import com.cordilleracoffee.product.domain.repository.ImageRepository;
import com.cordilleracoffee.product.domain.repository.ProductRepository;
import com.cordilleracoffee.product.domain.services.ProductService;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.CreateProductRequest;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.TagDto;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.*;

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

        CreateProductRequest productRequest = createProductCommand.request();

        productService.validateProduct(createProductCommand.userId(), productRequest.name(),
                productRequest.sku());

        Map<String, TemporalImage> imageMap = imageRepository.getTemporalImages(createProductCommand.userId());

        List<ProductImage> productImages = createProductImages(createProductCommand, imageMap);

        List<Variant> domainVariants = createDomainVariants(productRequest, imageMap);

        Product product = productService.createProduct(toDomainCommand(createProductCommand, productImages, domainVariants));

        moveProductImages(productImages, createProductCommand.userId());
        moveVariantImages(domainVariants, createProductCommand.userId());

        productRepository.save(product);

        return URI.create("http://localhost:8080/products/12345");
    }

    private void moveVariantImages(List<Variant> domainVariants, String userId) {
        for (var variant : domainVariants) {
            for (var variantImage : variant.getVariantImages()) {
                String finalUrl = fileStorageRepository.changeImageLocation("temp", "product",
                        variantImage.getName(), userId);

                variantImage.setUrl(finalUrl);
            }
        }
    }

    private void moveProductImages(List<ProductImage> productImages, String userId) {
        for (var productImage : productImages) {

            String finalUrl = fileStorageRepository.changeImageLocation("temp", "product",
                    productImage.getName(), userId);

            productImage.setUrl(finalUrl);
        }
    }


    private List<Variant> createDomainVariants(CreateProductRequest productRequest, Map<String, TemporalImage> imageMap) {
        List<Variant> domainVariants = new ArrayList<>();

        for (var variant : productRequest.variants()) {
            Set<VariantImage> variantImages = new HashSet<>();

            for (var variantImage : variant.images()) {

                if (!imageMap.containsKey(variantImage.id().toString())) {
                    throw new InvalidProductException("There is no image with id " + variantImage.id());
                }

                TemporalImage temporalImage = imageMap.get(variantImage.id().toString());

                var domainVariantImage = new VariantImage(null, temporalImage.name(),
                        variantImage.displayOrder(), variantImage.isPrimary(), temporalImage.url());

                variantImages.add(domainVariantImage);
            }

            var domainVariant = new Variant(
                    variant.name(), variant.description(),
                    new Stock((long) variant.stock()),
                    new Money(productRequest.basePrice().amount(), productRequest.basePrice().currency()),
                    variant.isPrimary(), new Sku(variant.sku()), variantImages);

            domainVariants.add(domainVariant);
        }
        return domainVariants;
    }

    private List<ProductImage> createProductImages(CreateProductCommand createProductCommand,
                                                   Map<String, TemporalImage> imageMap) {

        return createProductCommand.request().images().stream()
                .map(imageDto -> {
                    boolean imageNotPresent = !imageMap.containsKey(imageDto.id().toString());
                    if (imageNotPresent) {
                        throw new InvalidProductException("There is temporal image ids that are not present in the system " +
                                "(id: " + imageDto.id() + ")");
                    }
                    TemporalImage temporalImage = imageMap.get(imageDto.id().toString());

                    return new ProductImage(null, temporalImage.name(),
                            imageDto.displayOrder(), imageDto.isPrimary(), temporalImage.url());
                })
                .toList();
    }


    private CreateProduct toDomainCommand(CreateProductCommand createProductCommand,
                                          List<ProductImage> images, List<Variant> variants) {

        CreateProductRequest productRequest = createProductCommand.request();


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
