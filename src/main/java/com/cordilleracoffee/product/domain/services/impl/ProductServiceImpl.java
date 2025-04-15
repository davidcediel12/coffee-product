package com.cordilleracoffee.product.domain.services.impl;

import com.cordilleracoffee.product.domain.commands.CreateProduct;
import com.cordilleracoffee.product.domain.exception.InvalidProductException;
import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.domain.repository.ProductRepository;
import com.cordilleracoffee.product.domain.services.ProductService;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(CreateProduct createProduct) {

        return new Product.Builder(createProduct.name(), createProduct.description(),
                createProduct.userId(), createProduct.sku(),
                createProduct.categoryId(), new HashSet<>(createProduct.images()))
                .stock(createProduct.stock())
                .basePrice(createProduct.basePrice())
                .status(createProduct.status())
                .categoryId(createProduct.categoryId())
                .basePrice(createProduct.basePrice())
                .variants(new HashSet<>(createProduct.variants()))
                .tagIds(new HashSet<>(createProduct.tagsIds()))
                .build();
    }

    @Override
    public void validateProduct(String userId, String name, String sku) {

        if (productRepository.existsByUserAndName(userId, name)) {
            throw new InvalidProductException("There is an existent product with the same name");
        }

        if (productRepository.existByUserAndSku(userId, sku)) {
            throw new InvalidProductException("There is an existent product with the same sku");
        }
    }
}
