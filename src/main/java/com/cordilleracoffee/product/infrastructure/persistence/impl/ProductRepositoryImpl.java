package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.domain.repository.ProductRepository;
import com.cordilleracoffee.product.infrastructure.mappers.ProductMapper;
import com.cordilleracoffee.product.infrastructure.persistence.*;
import com.cordilleracoffee.product.infrastructure.persistence.entity.Tag;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final VariantJpaRepository variantJpaRepository;
    private final VariantImageJpaRepository variantImageJpaRepository;
    private final ProductImageJpaRepository productImageJpaRepository;

    private final ProductMapper productMapper;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository, CategoryJpaRepository categoryJpaRepository, TagJpaRepository tagJpaRepository, VariantJpaRepository variantJpaRepository, VariantImageJpaRepository variantImageJpaRepository, ProductImageJpaRepository productImageJpaRepository, ProductMapper productMapper) {
        this.productJpaRepository = productJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
        this.tagJpaRepository = tagJpaRepository;
        this.variantJpaRepository = variantJpaRepository;
        this.variantImageJpaRepository = variantImageJpaRepository;
        this.productImageJpaRepository = productImageJpaRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Product save(Product product) {
        var persistentProduct = productMapper.toJpaEntity(product);

        persistentProduct.setCategory(categoryJpaRepository.getReferenceById(product.getCategoryId()));

        Set<Tag> tags = new HashSet<>();

        for (Long tagId : product.getTagIds()) {
            tags.add(tagJpaRepository.getReferenceById(tagId));
        }
        persistentProduct.setTags(tags);


        var savedProduct = productJpaRepository.save(persistentProduct);
        product.setId(savedProduct.getId());

        for (var image : persistentProduct.getImages()) {
            image.setProduct(savedProduct);
        }

        productImageJpaRepository.saveAll(persistentProduct.getImages());

        for (var variant : product.getVariants()) {

            var jpaVariant = productMapper.toJpaEntity(variant);

            jpaVariant.setProduct(savedProduct);
            var savedVariant = variantJpaRepository.save(jpaVariant);

            variant.setId(savedVariant.getId());
            for (var image : Optional.ofNullable(jpaVariant.getVariantImages()).orElse(Collections.emptySet())) {
                image.setVariant(savedVariant);
            }
            variantImageJpaRepository.saveAll(jpaVariant.getVariantImages());
        }

        return product;
    }

    @Override
    public boolean existsByUserAndName(String userId, String name) {
        return productJpaRepository.existsBySellerIdAndName(userId, name);
    }

    @Override
    public boolean existByUserAndSku(String userId, String sku) {
        return productJpaRepository.existsBySellerIdAndSku(userId, sku);
    }
}
