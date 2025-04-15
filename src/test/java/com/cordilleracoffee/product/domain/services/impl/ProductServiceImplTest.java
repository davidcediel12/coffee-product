package com.cordilleracoffee.product.domain.services.impl;

import com.cordilleracoffee.product.domain.exception.InvalidProductException;
import com.cordilleracoffee.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;



    @Test
    void shouldReturnErrorWhenExistsAProductWithSameName() {


        when(productRepository.existsByUserAndName(anyString(), anyString())).thenReturn(true);

        assertThrows(InvalidProductException.class, () -> {
            productService.validateProduct("user-123", "product1", "sku1");
        });
    }


    @Test
    void shouldReturnErrorWhenExistsAProductWithSameSku() {

        when(productRepository.existsByUserAndName(anyString(), anyString())).thenReturn(false);
        when(productRepository.existByUserAndSku(anyString(), anyString())).thenReturn(true);

        assertThrows(InvalidProductException.class, () -> {
            productService.validateProduct("user-123", "product1", "sku1");
        });
    }


    @Test
    void shouldValidateProduct() {


        when(productRepository.existsByUserAndName(anyString(), anyString())).thenReturn(false);
        when(productRepository.existByUserAndSku(anyString(), anyString())).thenReturn(false);

        assertDoesNotThrow(() -> {
            productService.validateProduct("user-123", "product1", "sku1");
        });
    }

}