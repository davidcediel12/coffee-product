package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.domain.repository.ProductRepository;
import com.cordilleracoffee.product.infrastructure.mappers.ProductMapper;
import com.cordilleracoffee.product.infrastructure.persistence.CategoryJpaRepository;
import com.cordilleracoffee.product.infrastructure.persistence.ProductJpaRepository;
import com.cordilleracoffee.product.infrastructure.persistence.TagJpaRepository;
import com.cordilleracoffee.product.infrastructure.persistence.entity.Tag;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final ProductMapper productMapper;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository, CategoryJpaRepository categoryJpaRepository, TagJpaRepository tagJpaRepository, ProductMapper productMapper) {
        this.productJpaRepository = productJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
        this.tagJpaRepository = tagJpaRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Long save(Product product) {
        var persistentProduct = productMapper.toJpaEntity(product);

        persistentProduct.setCategory(categoryJpaRepository.getReferenceById(product.getCategoryId()));

        Set<Tag> tags = new HashSet<>();

        for(Long tagId : product.getTagIds()){
            tags.add(tagJpaRepository.getReferenceById(tagId));
        }
        persistentProduct.setTags(tags);

        return productJpaRepository.save(persistentProduct).getId();
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
