package com.antonio.pagamentos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pagamentosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Pagamentos")
                        .description("Documentacao da API de pagamentos")
                        .version("v1")
                        .contact(new Contact().name("Antonio")));
    }
}

