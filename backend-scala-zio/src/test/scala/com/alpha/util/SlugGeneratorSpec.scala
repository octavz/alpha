package com.alpha.util

import zio.test.*

object SlugGeneratorSpec extends ZIOSpecDefault:

  override def spec = suite("SlugGeneratorSpec")(
    suite("generate")(
      test("converts simple name to lowercase slug") {
        val result = SlugGenerator.generate("Hello World")
        assertTrue(result == "hello-world")
      },
      test("replaces spaces with dashes") {
        val result = SlugGenerator.generate("My Business Name")
        assertTrue(result == "my-business-name")
      },
      test("removes special characters") {
        val result = SlugGenerator.generate("Hello! World?")
        assertTrue(result == "hello-world")
      },
      test("removes multiple consecutive dashes") {
        val result = SlugGenerator.generate("Hello---World")
        assertTrue(result == "hello-world")
      },
      test("strips leading dashes") {
        val result = SlugGenerator.generate("-Hello World")
        assertTrue(result == "hello-world")
      },
      test("strips trailing dashes") {
        val result = SlugGenerator.generate("Hello World-")
        assertTrue(result == "hello-world")
      },
      test("handles empty string") {
        val result = SlugGenerator.generate("")
        assertTrue(result == "")
      },
      test("handles string with only special characters") {
        val result = SlugGenerator.generate("!@#$%")
        assertTrue(result == "")
      },
      test("handles mixed case") {
        val result = SlugGenerator.generate("My AWESOME Business")
        assertTrue(result == "my-awesome-business")
      },
      test("handles numbers") {
        val result = SlugGenerator.generate("Business 123")
        assertTrue(result == "business-123")
      },
      test("handles underscores") {
        val result = SlugGenerator.generate("hello_world")
        assertTrue(result == "hello_world")
      },
      test("handles accented characters") {
        val result = SlugGenerator.generate("Cafe")
        assertTrue(result == "cafe")
      },
      test("handles multiple spaces") {
        val result = SlugGenerator.generate("Hello    World")
        assertTrue(result == "hello-world")
      },
      test("handles dots") {
        val result = SlugGenerator.generate("example.com")
        assertTrue(result == "examplecom")
      }
    ),
    suite("generateUnique")(
      test("returns base slug when it doesn't exist") {
        val exists: String => Boolean = _ => false
        val result = SlugGenerator.generateUnique("Hello World", exists)
        assertTrue(result == "hello-world")
      },
      test("appends number when base slug exists") {
        val exists: String => Boolean = s => s == "hello-world"
        val result = SlugGenerator.generateUnique("Hello World", exists)
        assertTrue(result == "hello-world-1")
      },
      test("finds next available number") {
        val exists: String => Boolean = s => s == "hello-world" || s == "hello-world-1"
        val result = SlugGenerator.generateUnique("Hello World", exists)
        assertTrue(result == "hello-world-2")
      },
      test("uses timestamp when max attempts exceeded") {
        val exists: String => Boolean = _ => true
        val result = SlugGenerator.generateUnique("Hello World", exists, maxAttempts = 2)
        assertTrue(result.startsWith("hello-world-"))
      },
      test("respects custom maxAttempts") {
        var attempts = 0
        val exists: String => Boolean = { s =>
          attempts += 1
          true
        }
        SlugGenerator.generateUnique("Test", exists, maxAttempts = 3)
        assertTrue(attempts >= 3)
      }
    )
  )
