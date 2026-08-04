package com.micael.demo_park_api.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocOpenApiConfig {

    @Bean
    public OpenAPI openApi(){
        return new OpenAPI()
            .components(new Components().addSecuritySchemes("security", securityScheme()))
            .info(
                new Info()
                    .title("REST API - demo park")
                    .description("Api para gestão de estacionamento")
                    .version("v1")
            );
    }

    private SecurityScheme securityScheme(){
        return new SecurityScheme()
            .description("Insira um bearer token valido")
            .type(SecurityScheme.Type.HTTP)
            .in(SecurityScheme.In.HEADER)
            .scheme("bearer")
            .bearerFormat("JWT")
            .name("security");
    }
}
