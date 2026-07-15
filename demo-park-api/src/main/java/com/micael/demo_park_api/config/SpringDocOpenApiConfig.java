package com.micael.demo_park_api.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocOpenApiConfig {

    @Bean
    public OpenAPI openApi(){
        return new OpenAPI()
            .info(
                new Info()
                    .title("REST API - demo park")
                    .description("Api para gestão de estacionamento")
                    .version("v1")
            );
    }
}
