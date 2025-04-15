package com.cordilleracoffee.product.domain.services.impl;

import com.cordilleracoffee.product.domain.commands.CreateProduct;
import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.domain.services.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public Product createProduct(CreateProduct createProduct) {
        return null;
    }

    @Override
    public void validateProduct(String name, String sku) {

    }
}
