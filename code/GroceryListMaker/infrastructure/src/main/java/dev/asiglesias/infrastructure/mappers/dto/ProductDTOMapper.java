package dev.asiglesias.infrastructure.mappers.dto;

import dev.asiglesias.domain.MeasureUnit;
import dev.asiglesias.domain.Product;
import dev.asiglesias.infrastructure.rest.v1.dto.MeasureUnitDTO;
import dev.asiglesias.infrastructure.rest.v1.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ProductDTOMapper {

    ProductDTO toDto(Product product);

    MeasureUnit toDomainMeasure(MeasureUnitDTO measureUnitDTO);

}
