package dev.asiglesias.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @EqualsAndHashCode.Include private String name;

    private List<String> alternativeNames;

    private MeasureUnit preferredUnit;

}
