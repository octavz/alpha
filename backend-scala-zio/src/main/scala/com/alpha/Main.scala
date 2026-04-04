package com.alpha

import zio.*
import zio.http.*
import com.alpha.config.*
import com.alpha.repository.*
import com.alpha.service.*
import com.alpha.controller.*
import com.alpha.middleware.*
import com.alpha.provider.*

object Main extends ZIOAppDefault:

  private val appRoutes: URIO[
    HealthController & AuthController & CategoryController & RegionController &
    BusinessController & AppointmentController & ReviewController & ServiceController &
    BusinessHoursController,
    Routes[Any, Throwable]
  ] =
    for
      h <- ZIO.service[HealthController]
      a <- ZIO.service[AuthController]
      c <- ZIO.service[CategoryController]
      r <- ZIO.service[RegionController]
      b <- ZIO.service[BusinessController]
      ap <- ZIO.service[AppointmentController]
      rev <- ZIO.service[ReviewController]
      s <- ZIO.service[ServiceController]
      bh <- ZIO.service[BusinessHoursController]
    yield h.routes ++ a.routes ++ c.routes ++ r.routes ++ b.routes ++ ap.routes ++ rev.routes ++ s.routes ++ bh.routes ++ SwaggerController.routes

  override def run: ZIO[Any, Throwable, Unit] =
    (for
      routes <- appRoutes
      _ <- Server.serve(routes.sandbox @@ CorsMiddleware.cors)
    yield ()).provide(
      AppConfig.live,
      DatabaseConfig.postgresLayer,
      TimeProvider.live,
      UUIDProvider.live,
      UserRepository.layer,
      SessionRepository.layer,
      CategoryRepository.layer,
      RegionRepository.layer,
      BusinessRepository.layer,
      AppointmentRepository.layer,
      ReviewRepository.layer,
      ServiceRepository.layer,
      BusinessHoursRepository.layer,
      PasswordResetRepository.layer,
      EmailVerificationRepository.layer,
      AuthService.layer,
      CategoryService.layer,
      RegionService.layer,
      BusinessService.layer,
      AppointmentService.layer,
      ReviewService.layer,
      ServiceService.layer,
      BusinessHoursService.layer,
      HealthController.layer,
      AuthController.layer,
      CategoryController.layer,
      RegionController.layer,
      BusinessController.layer,
      AppointmentController.layer,
      ReviewController.layer,
      ServiceController.layer,
      BusinessHoursController.layer,
      Server.default
    )
