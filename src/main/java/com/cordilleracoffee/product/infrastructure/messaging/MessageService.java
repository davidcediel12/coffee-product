package com.cordilleracoffee.product.infrastructure.messaging;

import com.cordilleracoffee.product.domain.model.Product;

public interface MessageService {
    void sendNewProduct(Product product);
}
