package com.aiprocess.backendonia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI backendOnIaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Backend On IA API")
                        .description("API REST para categorias, usuários, templates, documentos, chats e ingestão de conhecimento.")
                        .version("v1")
                        .contact(new Contact().name("Codex / Backend On IA")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor local")
                ));
    }
}
