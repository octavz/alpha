rootProject.name = "alpha-backend"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.23"
        id("org.springframework.boot") version "3.2.5"
        id("io.spring.dependency-management") version "1.1.4"
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}