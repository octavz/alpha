package com.alpha

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class DatabaseConnectionTest {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `database connection should be established`() {
        val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
        assertNotNull(result)
        println("Database connection test passed: $result")
    }
}