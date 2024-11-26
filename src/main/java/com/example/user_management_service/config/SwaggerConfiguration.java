package com.example.user_management_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;


@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPIConfig() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(new Info()
                        .title("Falcon Api")
                        .version("1.0.1")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache License").url("https://apache.co")))
                .servers(List.of(
//                        new Server().url("http://localhost:8080").description("Falcon server")
                        new Server().url("https://falcon-api.etamin.agency").description("Falcon server")
                ));
    }
}