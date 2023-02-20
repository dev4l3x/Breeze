package dev.asiglesias.infrastructure.grocerylist.controllers.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {

    private String name;

    private List<String> alternativeNames;

    private MeasureUnitDTO preferredUnit;

}
