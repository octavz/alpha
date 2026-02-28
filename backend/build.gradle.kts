import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    kotlin("plugin.jpa") version "2.3.0"
    id("org.flywaydb.flyway") version "10.17.0"
}

group = "com.alpha"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
    jvmToolchain(25)
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Database
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    
    // Security - JWT
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("io.jsonwebtoken:jjwt-api:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.7")
    
    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")
    
    // Utilities
    implementation("org.apache.commons:commons-lang3:3.15.0")
    implementation("org.slf4j:slf4j-api:2.0.14")
    implementation("com.github.javafaker:javafaker:1.0.2") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    
    // Development
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:postgresql:1.20.2")
    testImplementation("org.testcontainers:junit-jupiter:1.20.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.0")
    testImplementation("io.kotest:kotest-assertions-core:5.9.0")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("io.mockk:mockk:1.13.12")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// Flyway configuration
flyway {
    url = "jdbc:postgresql://localhost:5433/alpha"
    user = "alpha_user"
    password = "alpha_password"
    schemas = arrayOf("public")
    locations = arrayOf("filesystem:src/main/resources/db/migration")
    baselineOnMigrate = true
    baselineVersion = "1"
}



// Application properties
springBoot {
    buildInfo()
}

// BootJar configuration
tasks.bootJar {
    archiveFileName.set("alpha-backend.jar")
}