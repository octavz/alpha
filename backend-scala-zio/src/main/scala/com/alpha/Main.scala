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

  override def run: ZIO[Any, Throwable, Unit] =
    TapirEndpoints.allRoutes.flatMap { routes =>
      Server.serve(routes @@ CorsMiddleware.cors)
    }.provide(
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
      Server.default
    )
