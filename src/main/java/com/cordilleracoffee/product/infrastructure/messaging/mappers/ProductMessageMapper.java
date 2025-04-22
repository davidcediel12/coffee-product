package com.cordilleracoffee.product.infrastructure.messaging.mappers;

import com.cordilleracoffee.product.domain.model.ProductImage;
import com.cordilleracoffee.product.domain.model.Variant;
import com.cordilleracoffee.product.domain.model.VariantImage;
import com.cordilleracoffee.product.infrastructure.messaging.dto.*;
import com.cordilleracoffee.product.infrastructure.persistence.entity.Category;
import com.cordilleracoffee.product.infrastructure.persistence.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMessageMapper {

    @Mapping(target = "sku", source = "sku.sku")
    @Mapping(target = "stock", source = "stock.amount")
    ProductMessage toProductMessage(com.cordilleracoffee.product.domain.model.Product product);

    @Mapping(target = "stock", source = "stock.amount")
    @Mapping(target = "sku", source = "sku.sku")
    @Mapping(target = "isPrimary", source = "primary")
    @Mapping(target = "images", source = "variantImages")
    VariantDto toVariantMessage(Variant variant);

    CategoryDto toCategoryMessage(Category category);

    TagDto toTagMessage(Tag tag);

    List<TagDto> toTagMessages(List<Tag> tag);

    @Mapping(target = "isPrimary", source = ".", qualifiedByName = "variantImageToPrimary")
    ImageDto toImageMessage(VariantImage image);

    @Mapping(target = "isPrimary", source = ".", qualifiedByName = "productImageToPrimary")
    ImageDto toImageMessage(ProductImage image);

    @Named("productImageToPrimary")
    default Boolean toPrimary(ProductImage productImage) {
        return productImage.isPrimary();
    }

    @Named("variantImageToPrimary")
    default Boolean toPrimary(VariantImage variantImage) {
        return variantImage.isPrimary();
    }
}
