package dev.asiglesias.infrastructure.rest.v1;

import dev.asiglesias.application.CreateProductUseCase;
import dev.asiglesias.infrastructure.mappers.dto.ProductDTOMapper;
import dev.asiglesias.infrastructure.rest.v1.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;

    private final ProductDTOMapper mapper;

    @PostMapping
    ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productToCreate) {
        createProductUseCase.create(
                productToCreate.getName(),
                productToCreate.getAlternativeNames(),
                mapper.toDomainMeasure(productToCreate.getPreferredUnit())
        );
        return ResponseEntity.of(Optional.empty());
    }

}
