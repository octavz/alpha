package com.alpha.dto

import zio.test.*
import zio.json.*

object PaginationSpec extends ZIOSpecDefault:

  override def spec = suite("PaginationSpec")(
    suite("PaginatedResponse")(
      test("calculates totalPages correctly when total is divisible by pageSize") {
        val response = PaginatedResponse(List("a", "b", "c", "d", "d"), 10, 1, 5)
        assertTrue(response.totalPages == 2)
      },
      test("calculates totalPages correctly when total is not divisible by pageSize") {
        val response = PaginatedResponse(List("a", "b", "c"), 7, 1, 3)
        assertTrue(response.totalPages == 3)
      },
      test("returns at least 1 totalPages when total is 0") {
        val response = PaginatedResponse(List.empty[String], 0, 1, 10)
        assertTrue(response.totalPages == 1)
      },
      test("calculates totalPages for single page") {
        val response = PaginatedResponse(List("a", "b"), 2, 1, 10)
        assertTrue(response.totalPages == 1)
      },
      test("calculates totalPages for exact page boundary") {
        val response = PaginatedResponse(List.fill(20)("item"), 20, 1, 20)
        assertTrue(response.totalPages == 1)
      },
      test("calculates totalPages for one over boundary") {
        val response = PaginatedResponse(List.fill(21)("item"), 21, 1, 20)
        assertTrue(response.totalPages == 2)
      },
      test("preserves items list") {
        val items = List("first", "second", "third")
        val response = PaginatedResponse(items, 3, 1, 10)
        assertTrue(response.items == items)
      },
      test("preserves total count") {
        val response = PaginatedResponse(List("a"), 42, 1, 10)
        assertTrue(response.total == 42)
      },
      test("preserves page number") {
        val response = PaginatedResponse(List("a"), 10, 3, 5)
        assertTrue(response.page == 3)
      },
      test("preserves pageSize") {
        val response = PaginatedResponse(List("a"), 10, 1, 25)
        assertTrue(response.pageSize == 25)
      },
      test("encodes to JSON with all fields") {
        val response = PaginatedResponse(List("a", "b"), 5, 1, 2)
        val json = response.toJson
        assertTrue(json.contains("items"))
        assertTrue(json.contains("total"))
        assertTrue(json.contains("page"))
        assertTrue(json.contains("pageSize"))
        assertTrue(json.contains("totalPages"))
      },
      test("decodes from JSON") {
        val json = """{"items":[1,2,3],"total":3,"page":1,"pageSize":10,"totalPages":1}"""
        val decoded = json.fromJson[PaginatedResponse[Int]]
        assertTrue(decoded.map(_.items) == Right(List(1, 2, 3)))
        assertTrue(decoded.map(_.total) == Right(3))
        assertTrue(decoded.map(_.totalPages) == Right(1))
      },
      test("round-trip encode/decode") {
        val response = PaginatedResponse(List("x", "y"), 2, 1, 10)
        val decoded = response.toJson.fromJson[PaginatedResponse[String]]
        assertTrue(decoded == Right(response))
      }
    ),
    suite("PaginationParams")(
      test("has default page of 1") {
        val params = PaginationParams()
        assertTrue(params.page == 1)
      },
      test("has default pageSize of 20") {
        val params = PaginationParams()
        assertTrue(params.pageSize == 20)
      },
      test("accepts custom page") {
        val params = PaginationParams(page = 5)
        assertTrue(params.page == 5 && params.pageSize == 20)
      },
      test("accepts custom pageSize") {
        val params = PaginationParams(pageSize = 50)
        assertTrue(params.page == 1 && params.pageSize == 50)
      },
      test("accepts both custom values") {
        val params = PaginationParams(page = 3, pageSize = 10)
        assertTrue(params.page == 3 && params.pageSize == 10)
      }
    )
  )
