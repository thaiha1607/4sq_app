package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Product;
import com.foursquare.server.domain.Tag;
import com.foursquare.server.service.dto.ProductDTO;
import com.foursquare.server.service.dto.TagDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagNameSet")
    ProductDTO toDto(Product s);

    @Mapping(target = "removeTag", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Named("tagName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TagDTO toDtoTagName(Tag tag);

    @Named("tagNameSet")
    default Set<TagDTO> toDtoTagNameSet(Set<Tag> tag) {
        return tag.stream().map(this::toDtoTagName).collect(Collectors.toSet());
    }
}
