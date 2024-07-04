package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Colour;
import com.foursquare.server.domain.Product;
import com.foursquare.server.domain.ProductCategory;
import com.foursquare.server.service.dto.ColourDTO;
import com.foursquare.server.service.dto.ProductCategoryDTO;
import com.foursquare.server.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductCategory} and its DTO {@link ProductCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductCategoryMapper extends EntityMapper<ProductCategoryDTO, ProductCategory> {
    @Mapping(target = "colour", source = "colour", qualifiedByName = "colourHexCode")
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    ProductCategoryDTO toDto(ProductCategory s);

    @Named("colourHexCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "hexCode", source = "hexCode")
    ColourDTO toDtoColourHexCode(Colour colour);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
