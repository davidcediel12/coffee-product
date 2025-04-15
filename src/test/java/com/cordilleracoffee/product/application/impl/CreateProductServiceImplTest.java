package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.application.FileStorageRepository;
import com.cordilleracoffee.product.application.command.CreateProductCommand;
import com.cordilleracoffee.product.domain.exception.InvalidProductException;
import com.cordilleracoffee.product.domain.model.TemporalImage;
import com.cordilleracoffee.product.domain.model.UserRole;
import com.cordilleracoffee.product.domain.repository.ImageRepository;
import com.cordilleracoffee.product.domain.repository.ProductRepository;
import com.cordilleracoffee.product.domain.services.ProductService;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.CreateProductRequest;
import com.cordilleracoffee.product.utils.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductServiceImplTest {

    @Mock
    ProductService productService;

    @Mock
    ImageRepository imageRepository;

    @Mock
    FileStorageRepository fileStorageRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    CreateProductServiceImpl createProductService;


    @Test
    void shouldSaveProduct() {

        when(imageRepository.getTemporalImages(anyString())).thenReturn(Map.of(
                "b08ee4b0-22bb-446c-bd33-0343a77e9b11", new TemporalImage("b08ee4b0-22bb-446c-bd33-0343a77e9b11",
                        "other", "url", "user-123")
        ));

        when(productService.createProduct(any())).thenReturn(TestDataFactory.validProduct());


        CreateProductRequest productRequest = TestDataFactory.validCreateProductRequest();

        URI uri = createProductService.createProduct(
                new CreateProductCommand(productRequest, "user-123",
                        List.of(UserRole.SELLER))
        );

        assertThat(uri).isEqualTo(URI.create("http://localhost:8080/products/12345"));

        verify(productService).validateProduct(anyString(), anyString(), anyString());
        verify(productService).createProduct(any());

        verify(fileStorageRepository, atLeastOnce()).changeImageLocation(eq("temp"), eq("product-assets"), anyString(), eq("user-123"));
        verify(productRepository).save(any());
    }

    @Test
    void shouldReturnErrorWhenImagesNotExist() {

        CreateProductCommand createProductCommand = new CreateProductCommand(TestDataFactory.validCreateProductRequest(),
                "user-123", List.of(UserRole.SELLER));


        when(imageRepository.getTemporalImages(anyString())).thenReturn(Map.of(
                "other-image", new TemporalImage("other-image",
                        "other", "url", "user-123")
        ));

        assertThrows(InvalidProductException.class, () -> createProductService.createProduct(
                createProductCommand
        ));
    }


}
