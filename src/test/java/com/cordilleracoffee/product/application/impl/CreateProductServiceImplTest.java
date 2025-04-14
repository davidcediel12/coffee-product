package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.application.command.CreateProductCommand;
import com.cordilleracoffee.product.domain.model.UserRole;
import com.cordilleracoffee.product.domain.services.ProductService;
import com.cordilleracoffee.product.utils.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateProductServiceImplTest {

    @Mock
    ProductService productService;

    @InjectMocks
    CreateProductServiceImpl createProductService;

    @Test
    void shouldSaveProduct() {
        URI uri = createProductService.createProduct(
                new CreateProductCommand(TestDataFactory.validCreateProductRequest(), "user-123",
                        List.of(UserRole.SELLER))
        );

        assertThat(uri).isEqualTo(URI.create("http://localhost:8080/products/12345"));

        verify(productService).createProduct(any());
    }
}
