package dev.asiglesias.infrastructure.configuration;

import dev.asiglesias.application.CreateProductUseCase;
import dev.asiglesias.application.CreateProductUseCaseImpl;
import dev.asiglesias.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCaseBeanConfiguration {

    private final ProductRepository productRepository;

    @Bean
    CreateProductUseCase createProductUseCase() {
        return new CreateProductUseCaseImpl(productRepository);
    }
}
