package dev.asiglesias.infrastructure.entity.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("products")
public class ProductEntity {

    @Id
    private String id;

    private String name;

    private List<String> alternativeNames;

    private MeasureUnit preferredUnit;

}
