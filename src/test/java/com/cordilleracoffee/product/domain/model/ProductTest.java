package com.cordilleracoffee.product.domain.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static com.cordilleracoffee.product.utils.TestDataFactory.validProductVariant;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {


    @Test
    void shouldReturnErrorWhenCreateProductWithBasePriceAndVariants() {

        Product.Builder builder = initialProduct()
                .basePrice(new Money(BigDecimal.TEN, "EUR"))
                .variants(Set.of(validProductVariant()));
        
        assertThrows(IllegalArgumentException.class, builder::build);
    }


    @Test
    void shouldReturnErrorWhenCreateProductWithNoBasePriceAndNoVariants() {

        Product.Builder builder = initialProduct();
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void shouldReturnErrorWhenCreateProductWithNoImages() {

        Product.Builder productBuilder = initialProduct()
                .basePrice(new Money(BigDecimal.TEN, "EUR"))
                .images(Collections.emptySet());

        assertThrows(IllegalArgumentException.class, productBuilder::build);
    }

    @Test
    void shouldCreateValidProductWithVariants() {

        assertDoesNotThrow(() -> initialProduct()
                .variants(Set.of(validProductVariant())));
    }


    @Test
    void shouldCreateValidProductWithoutVariants() {

        assertDoesNotThrow(() -> initialProduct()
                .basePrice(new Money(BigDecimal.TEN, "EUR"))
                .stock(new Stock(10L)));
    }


    private static Product.@NotNull Builder initialProduct() {
        return new Product.Builder(
                "product", "desc", "user-123", new Sku("SKU"), 1L,
                Set.of(new ProductImage(1L, 1, true, "url.com")));
    }


}