package com.alpha.dto

import zio.json.*

case class PaginatedResponse[T](
  items: List[T],
  total: Int,
  page: Int,
  pageSize: Int,
  totalPages: Int
)

object PaginatedResponse:
  given [T: JsonEncoder]: JsonEncoder[PaginatedResponse[T]] = DeriveJsonEncoder.gen
  given [T: JsonDecoder]: JsonDecoder[PaginatedResponse[T]] = DeriveJsonDecoder.gen

  def apply[T](items: List[T], total: Int, page: Int, pageSize: Int): PaginatedResponse[T] =
    new PaginatedResponse(
      items = items,
      total = total,
      page = page,
      pageSize = pageSize,
      totalPages = math.max(1, (total + pageSize - 1) / pageSize)
    )

case class PaginationParams(
  page: Int = 1,
  pageSize: Int = 20
)
