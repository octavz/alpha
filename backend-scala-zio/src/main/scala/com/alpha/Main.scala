package com.alpha

import zio.*
import zio.http.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import com.alpha.config.*
import com.alpha.repository.*
import com.alpha.service.*
import com.alpha.controller.*
import com.alpha.middleware.*
import com.alpha.provider.*

object Main extends ZIOAppDefault:

  private val allEndpointsForSwagger: List[ZServerEndpoint[Any, Any]] =
    AuthEndpoints.endpoints.map(_.asInstanceOf[ZServerEndpoint[Any, Any]]) ++
      CategoryEndpoints.endpoints.map(_.asInstanceOf[ZServerEndpoint[Any, Any]]) ++
      RegionEndpoints.endpoints.map(_.asInstanceOf[ZServerEndpoint[Any, Any]]) ++
      BusinessEndpoints.endpoints.map(_.asInstanceOf[ZServerEndpoint[Any, Any]]) ++
      AppointmentEndpoints.endpoints.map(_.asInstanceOf[ZServerEndpoint[Any, Any]]) ++
      ReviewEndpoints.endpoints.map(_.asInstanceOf[ZServerEndpoint[Any, Any]]) ++
      ServiceEndpoints.endpoints.map(_.asInstanceOf[ZServerEndpoint[Any, Any]]) ++
      BusinessHoursEndpoints.endpoints.map(_.asInstanceOf[ZServerEndpoint[Any, Any]])

  private val swaggerRoutes: Routes[Any, Response] =
    ZioHttpInterpreter().toHttp(SwaggerInterpreter().fromServerEndpoints(allEndpointsForSwagger, "Alpha API", "1.0.0"))

  override def run: ZIO[Any, Throwable, Unit] =
    (for
      auth <- AuthEndpoints.routes
      cat  <- CategoryEndpoints.routes
      reg  <- RegionEndpoints.routes
      biz  <- BusinessEndpoints.routes
      appt <- AppointmentEndpoints.routes
      rev  <- ReviewEndpoints.routes
      svc  <- ServiceEndpoints.routes
      hrs  <- BusinessHoursEndpoints.routes
      all   = auth ++ cat ++ reg ++ biz ++ appt ++ rev ++ svc ++ hrs ++ HealthEndpoint.routes ++ swaggerRoutes
      _    <- Server.serve(all @@ CorsMiddleware.cors)
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
      EmailService.live,
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
