package com.alpha.controller

import zio.*
import zio.http.*
import zio.json.*
import com.alpha.service.*
import com.alpha.dto.*
import com.alpha.domain.model.*
import java.util.UUID

class CategoryController(categoryService: CategoryService):

  private def jsonResponse[T: JsonEncoder](data: ApiResponse[T]): Response =
    Response.json(JsonEncoder[ApiResponse[T]].encodeJson(data, None))

  private def errorResponse(message: String, status: Status): Response =
    Response.text(message).status(status)

  val routes = Routes(
    Method.GET / "api" / "v1" / "categories" -> handler(handleGetAll()),
    Method.GET / "api" / "v1" / "categories" / string("id") -> handler(handleGetById),
    Method.POST / "api" / "v1" / "categories" -> handler(handleCreate),
    Method.PUT / "api" / "v1" / "categories" / string("id") -> handler(handleUpdate),
    Method.DELETE / "api" / "v1" / "categories" / string("id") -> handler(handleDelete)
  )

  private def handleGetAll(): Task[Response] =
    categoryService.getAllCategories
      .map(categories => jsonResponse(ApiResponse.successList(categories)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.InternalServerError)))

  private def handleGetById(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      categoryService.getCategory(uuid).map {
        case Some(category) => jsonResponse(ApiResponse.success(category))
        case None => errorResponse("Category not found", Status.NotFound)
      }
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleCreate(request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[CreateCategoryRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      created <- categoryService.createCategory(req)
    yield jsonResponse(ApiResponse.success(created)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleUpdate(id: String, request: Request): Task[Response] =
    (for
      body <- request.body.asString
      req <- ZIO.fromEither(JsonDecoder[UpdateCategoryRequest].decodeJson(body)).orElseFail(new Exception("Invalid request body"))
      uuid <- ZIO.attempt(UUID.fromString(id))
      updated <- categoryService.updateCategory(uuid, req)
    yield jsonResponse(ApiResponse.success(updated)))
      .catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

  private def handleDelete(id: String, req: Request): Task[Response] =
    ZIO.attempt(UUID.fromString(id)).flatMap { uuid =>
      categoryService.deleteCategory(uuid).as(Response.status(Status.NoContent))
    }.catchAll(error => ZIO.succeed(errorResponse(error.getMessage, Status.BadRequest)))

object CategoryController:
  val layer: ZLayer[CategoryService, Nothing, CategoryController] =
    ZLayer.fromFunction(new CategoryController(_))
