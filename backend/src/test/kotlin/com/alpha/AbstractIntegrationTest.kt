package com.alpha

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest
abstract class AbstractIntegrationTest {

    companion object {
        @JvmStatic
        private var embeddedPostgres: EmbeddedPostgres? = null

        @JvmStatic
        @BeforeAll
        fun setupDatabase() {
            if (embeddedPostgres == null) {
                embeddedPostgres = EmbeddedPostgres.builder().start()
            }
            Runtime.getRuntime().addShutdownHook(Thread {
                try {
                    embeddedPostgres?.close()
                } catch (_: Exception) {
                }
            })
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            if (embeddedPostgres == null) {
                embeddedPostgres = EmbeddedPostgres.builder().start()
            }
            val ep = embeddedPostgres!!
            registry.add("spring.datasource.url") { ep.getJdbcUrl("postgres", "postgres") }
            registry.add("spring.datasource.username") { "postgres" }
            registry.add("spring.datasource.password") { "postgres" }
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
            registry.add("spring.flyway.enabled") { false }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }
}
