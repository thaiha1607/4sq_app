package com.foursquare.server.service.mapper;

import com.foursquare.server.domain.ProductCategory;
import com.foursquare.server.domain.ProductQuantity;
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.service.dto.ProductCategoryDTO;
import com.foursquare.server.service.dto.ProductQuantityDTO;
import com.foursquare.server.service.dto.WorkingUnitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductQuantity} and its DTO {@link ProductQuantityDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductQuantityMapper extends EntityMapper<ProductQuantityDTO, ProductQuantity> {
    @Mapping(target = "workingUnit", source = "workingUnit", qualifiedByName = "workingUnitName")
    @Mapping(target = "productCategory", source = "productCategory", qualifiedByName = "productCategoryName")
    ProductQuantityDTO toDto(ProductQuantity s);

    @Named("workingUnitName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WorkingUnitDTO toDtoWorkingUnitName(WorkingUnit workingUnit);

    @Named("productCategoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductCategoryDTO toDtoProductCategoryName(ProductCategory productCategory);
}
