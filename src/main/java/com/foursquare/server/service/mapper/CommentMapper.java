package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Comment;
import com.foursquare.server.domain.Product;
import com.foursquare.server.domain.User;
import com.foursquare.server.service.dto.CommentDTO;
import com.foursquare.server.service.dto.ProductDTO;
import com.foursquare.server.service.dto.UserDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    CommentDTO toDto(Comment s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
