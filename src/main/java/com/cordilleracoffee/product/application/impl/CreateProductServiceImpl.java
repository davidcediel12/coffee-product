package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.application.CreateProductService;
import com.cordilleracoffee.product.application.FileStorageRepository;
import com.cordilleracoffee.product.application.annotation.UseCase;
import com.cordilleracoffee.product.application.command.CreateProductCommand;
import com.cordilleracoffee.product.domain.commands.CreateProduct;
import com.cordilleracoffee.product.domain.exception.InvalidProductException;
import com.cordilleracoffee.product.domain.model.*;
import com.cordilleracoffee.product.domain.repository.CategoryRepository;
import com.cordilleracoffee.product.domain.repository.ImageRepository;
import com.cordilleracoffee.product.domain.repository.ProductRepository;
import com.cordilleracoffee.product.domain.repository.TagRepository;
import com.cordilleracoffee.product.domain.services.ProductService;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.CreateProductRequest;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.TagDto;
import com.cordilleracoffee.product.infrastructure.messaging.MessageService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.*;

@UseCase
public class CreateProductServiceImpl implements CreateProductService {

    private final ProductService productService;
    private final ImageRepository imageRepository;
    private final FileStorageRepository fileStorageRepository;
    private final ProductRepository productRepository;
    private final MessageService messageService;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public CreateProductServiceImpl(ProductService productService, ImageRepository imageRepository, FileStorageRepository fileStorageRepository, ProductRepository productRepository, MessageService messageService, CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.productService = productService;
        this.imageRepository = imageRepository;
        this.fileStorageRepository = fileStorageRepository;
        this.productRepository = productRepository;
        this.messageService = messageService;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public Long createProduct(@Valid CreateProductCommand createProductCommand) {

        CreateProductRequest productRequest = createProductCommand.request();

        validateAttributes(createProductCommand, productRequest);

        Map<String, TemporalImage> imageMap = imageRepository.getTemporalImages(createProductCommand.userId());

        List<ProductImage> productImages = createProductImages(createProductCommand, imageMap);

        List<Variant> domainVariants = createDomainVariants(productRequest, imageMap);

        Product product = productService.createProduct(toDomainCommand(createProductCommand, productImages, domainVariants));

        moveProductImages(productImages, createProductCommand.userId());
        moveVariantImages(domainVariants, createProductCommand.userId());

        product = productRepository.save(product);

        messageService.sendNewProduct(product);
        return product.getId();
    }

    private void validateAttributes(CreateProductCommand createProductCommand, CreateProductRequest productRequest) {
        productService.validateProduct(
                createProductCommand.userId(), productRequest.name(),
                productRequest.sku());

        List<Long> tagIds = productRequest.tags().stream()
                .map(TagDto::id)
                .toList();

        boolean categoryOrTagNotExists =
                !tagRepository.existsAll(tagIds) || !categoryRepository.exists(productRequest.category().id());

        if(categoryOrTagNotExists){
            throw new InvalidProductException("Category and tag must exists to create the product");
        }
    }

    private void moveVariantImages(List<Variant> domainVariants, String userId) {
        for (var variant : domainVariants) {
            for (var variantImage : variant.getVariantImages()) {

                String finalImageName = Path.of(userId, variantImage.getName()).toString();

                String finalUrl = fileStorageRepository.changeImageLocation("temp", "product-assets",
                        variantImage.getName(), finalImageName);

                variantImage.setUrl(finalUrl);
                variantImage.setName(finalImageName);
            }
        }
    }

    private void moveProductImages(List<ProductImage> productImages, String userId) {
        for (var productImage : productImages) {

            String finalImageName = Path.of(userId, productImage.getName()).toString();

            String finalUrl = fileStorageRepository.changeImageLocation("temp", "product-assets",
                    productImage.getName(), finalImageName);

            productImage.setUrl(finalUrl);
            productImage.setName(finalImageName);
        }
    }


    private List<Variant> createDomainVariants(CreateProductRequest productRequest, Map<String, TemporalImage> imageMap) {

        if (productRequest.variants() == null) {
            return Collections.emptyList();
        }

        List<Variant> domainVariants = new ArrayList<>();

        for (var variant : productRequest.variants()) {
            Set<VariantImage> variantImages = new HashSet<>();

            for (var variantImage : variant.images()) {

                if (!imageMap.containsKey(variantImage.id().toString())) {
                    throw new InvalidProductException("There is temporal image ids that are not present in the system " + variantImage.id());
                }

                TemporalImage temporalImage = imageMap.get(variantImage.id().toString());

                var domainVariantImage = new VariantImage(null, temporalImage.name(),
                        variantImage.displayOrder(), variantImage.isPrimary(), temporalImage.url());

                variantImages.add(domainVariantImage);
            }

            var domainVariant = new Variant(null,
                    variant.name(), variant.description(),
                    new Stock((long) variant.stock()),
                    new Money(variant.basePrice().amount(), variant.basePrice().currency()),
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

        Money productBasePrice = null;

        if (productRequest.basePrice() != null) {
            productBasePrice = new Money(productRequest.basePrice().amount(), productRequest.basePrice().currency());
        }

        Stock productStock = null;
        if(productRequest.stock() != null) {
            productStock = new Stock(Long.valueOf(productRequest.stock()));
        }
        return new CreateProduct(
                createProductCommand.userId(), productRequest.name(),
                productRequest.description(), productRequest.category().id(),
                new Sku(productRequest.sku()), productStock,
                productRequest.status(),
                productBasePrice,
                images, variants, tagIds
        );
    }
}
