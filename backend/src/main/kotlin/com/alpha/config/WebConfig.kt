package com.alpha.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig {
    
    @Value("\${app.cors.allowed-origins}")
    private lateinit var allowedOrigins: String
    
    @Value("\${app.cors.allowed-methods}")
    private lateinit var allowedMethods: String
    
    @Value("\${app.cors.allowed-headers}")
    private lateinit var allowedHeaders: String
    
    @Value("\${app.cors.allow-credentials}")
    private val allowCredentials: Boolean = true
    
    @Value("\${app.cors.max-age}")
    private val maxAge: Long = 3600
    
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(*allowedOrigins.split(",").toTypedArray())
                    .allowedMethods(*allowedMethods.split(",").toTypedArray())
                    .allowedHeaders(*allowedHeaders.split(",").toTypedArray())
                    .allowCredentials(allowCredentials)
                    .maxAge(maxAge)
                
                registry.addMapping("/v3/api-docs/**")
                    .allowedOrigins("*")
                
                registry.addMapping("/swagger-ui/**")
                    .allowedOrigins("*")
            }
        }
    }
}