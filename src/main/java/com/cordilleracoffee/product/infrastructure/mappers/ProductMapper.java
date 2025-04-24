package com.cordilleracoffee.product.infrastructure.mappers;


import com.cordilleracoffee.product.infrastructure.persistence.entity.Product;
import com.cordilleracoffee.product.infrastructure.persistence.entity.ProductImage;
import com.cordilleracoffee.product.infrastructure.persistence.entity.Variant;
import com.cordilleracoffee.product.infrastructure.persistence.entity.VariantImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", source = "sku.sku")
    @Mapping(target = "currency", source = "basePrice.currency")
    @Mapping(target = "basePrice", source = "basePrice.amount")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "variants", ignore = true)
    Product toJpaEntity(com.cordilleracoffee.product.domain.model.Product domainProduct);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductImage toJpaEntity(com.cordilleracoffee.product.domain.model.ProductImage domainProductImage);

    Set<ProductImage> toPersistentImages(Set<com.cordilleracoffee.product.domain.model.ProductImage> productImages);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stock", source = "stock.amount")
    @Mapping(target = "basePrice", source = "basePrice.amount")
    @Mapping(target = "currency", source = "basePrice.currency")
    @Mapping(target = "sku", source = "sku.sku")
    @Mapping(target = "product", ignore = true)
    Variant toJpaEntity(com.cordilleracoffee.product.domain.model.Variant domainVariant);

    List<Variant> toPersistentVariants(Set<com.cordilleracoffee.product.domain.model.Variant> domainVariants);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "variant", ignore = true)
    VariantImage toJpaEntity(com.cordilleracoffee.product.domain.model.VariantImage domainVariantImage);

    Set<VariantImage> toPersistentVariantImages(Set<com.cordilleracoffee.product.domain.model.VariantImage> variantImages);
}
