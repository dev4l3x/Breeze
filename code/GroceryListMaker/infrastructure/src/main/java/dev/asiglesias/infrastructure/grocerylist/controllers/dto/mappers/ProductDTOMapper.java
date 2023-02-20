package dev.asiglesias.infrastructure.grocerylist.controllers.dto.mappers;

import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.domain.Product;
import dev.asiglesias.infrastructure.grocerylist.controllers.dto.MeasureUnitDTO;
import dev.asiglesias.infrastructure.grocerylist.controllers.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductDTOMapper {

    ProductDTO toDto(Product product);

    MeasureUnit toDomainMeasure(MeasureUnitDTO measureUnitDTO);

}
