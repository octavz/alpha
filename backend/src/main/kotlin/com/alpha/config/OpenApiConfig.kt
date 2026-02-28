package com.alpha.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    
    @Value("\${app.name}")
    private lateinit var appName: String
    
    @Value("\${app.version}")
    private lateinit var appVersion: String
    
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("$appName API")
                    .version(appVersion)
                    .description("Business Directory Platform API")
                    .contact(
                        Contact()
                            .name("Alpha Team")
                            .email("support@businessdirectory.com")
                    )
                    .license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
            .addServersItem(
                Server()
                    .url("http://localhost:3000")
                    .description("Development Server")
            )
            .addServersItem(
                Server()
                    .url("https://api.businessdirectory.com")
                    .description("Production Server")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearerAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Enter JWT token")
                    )
            )
            .addSecurityItem(
                SecurityRequirement()
                    .addList("bearerAuth")
            )
    }
}