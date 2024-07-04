package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Product;
import com.foursquare.server.domain.ProductImage;
import com.foursquare.server.service.dto.ProductDTO;
import com.foursquare.server.service.dto.ProductImageDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductImage} and its DTO {@link ProductImageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductImageMapper extends EntityMapper<ProductImageDTO, ProductImage> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    ProductImageDTO toDto(ProductImage s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
