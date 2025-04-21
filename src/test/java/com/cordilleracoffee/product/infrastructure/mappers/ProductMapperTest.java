package com.cordilleracoffee.product.infrastructure.mappers;

import com.cordilleracoffee.product.domain.model.Product;
import com.cordilleracoffee.product.infrastructure.persistence.entity.ProductImage;
import com.cordilleracoffee.product.utils.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    ProductMapperImpl productMapper = new ProductMapperImpl();

    @Test
    void shouldMapProductCorrectly() {

        Product product = TestDataFactory.validProduct();
        product.setVariants(Collections.emptySet());

        var actualEntity = productMapper.toJpaEntity(product);


        assertThat(actualEntity.getName()).isEqualTo(product.getName());
        assertThat(actualEntity.getDescription()).isEqualTo(product.getDescription());
        assertThat(actualEntity.getSku()).isEqualTo(product.getSku().sku());
        assertThat(actualEntity.getSellerId()).isEqualTo(product.getSellerId());
        assertThat(actualEntity.getStatus()).isEqualTo(product.getStatus().toString());
        assertThat(actualEntity.getCurrency()).isEqualTo(product.getBasePrice().currency());
        assertThat(actualEntity.getBasePrice()).isEqualTo(product.getBasePrice().amount());
        ProductImage productImage = actualEntity.getImages().stream().findFirst().orElseThrow();
        com.cordilleracoffee.product.domain.model.ProductImage domainImage = product.getImages().stream().findFirst().orElseThrow();
        assertThat(productImage.isPrimary()).isEqualTo(domainImage.isPrimary());


    }

}