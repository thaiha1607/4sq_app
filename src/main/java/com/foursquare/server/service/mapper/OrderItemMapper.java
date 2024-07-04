package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.Order;
import com.foursquare.server.domain.OrderItem;
import com.foursquare.server.domain.ProductCategory;
import com.foursquare.server.service.dto.OrderDTO;
import com.foursquare.server.service.dto.OrderItemDTO;
import com.foursquare.server.service.dto.ProductCategoryDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {
    @Mapping(target = "productCategory", source = "productCategory", qualifiedByName = "productCategoryName")
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    OrderItemDTO toDto(OrderItem s);

    @Named("productCategoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductCategoryDTO toDtoProductCategoryName(ProductCategory productCategory);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
