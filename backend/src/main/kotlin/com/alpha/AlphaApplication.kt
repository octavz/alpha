package com.alpha

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class AlphaApplication

fun main(args: Array<String>) {
    runApplication<AlphaApplication>(*args)
}