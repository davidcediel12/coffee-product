package com.cordilleracoffee.product.domain.services.impl;

import com.cordilleracoffee.product.domain.exception.InvalidProductException;
import com.cordilleracoffee.product.domain.commands.CreateProduct;
import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.domain.repository.ProductRepository;
import com.cordilleracoffee.product.domain.services.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(CreateProduct createProduct) {
        return null;
    }

    @Override
    public void validateProduct(String userId, String name, String sku) {

        if(productRepository.existsByUserAndName(userId, name) ){
            throw new InvalidProductException("There is an existent product with the same name");
        }

        if(productRepository.existByUserAndSku(userId, sku)){
            throw new InvalidProductException("There is an existent product with the same sku");
        }
    }
}
