package dev.asiglesias.infrastructure.rest.v1.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ProductDTO {

    private String name;

    private List<String> alternativeNames;

    @NotBlank
    private MeasureUnitDTO preferredUnit;

}
