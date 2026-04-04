package com.alpha.repository

import io.getquill.*
import java.util.UUID

type PostgresCtx = PostgresJdbcContext[SnakeCase]

object PostgresCtx:
  def apply(ds: javax.sql.DataSource): PostgresCtx = 
    new PostgresJdbcContext(SnakeCase, ds)
