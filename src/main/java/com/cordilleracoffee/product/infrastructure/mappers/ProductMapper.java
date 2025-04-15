package com.cordilleracoffee.product.infrastructure.mappers;


import com.cordilleracoffee.product.infrastructure.persistence.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", source = "sku.sku")
    @Mapping(target = "stock", source = "stock.amount")
    @Mapping(target = "currency", source = "basePrice.currency")
    @Mapping(target = "basePrice", source = "basePrice.amount")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Product toJpaEntity(com.cordilleracoffee.product.domain.model.Product domainProduct);
}
