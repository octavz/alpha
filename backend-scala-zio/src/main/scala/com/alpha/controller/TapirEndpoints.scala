package com.alpha.controller

import sttp.tapir.ztapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio.*
import zio.http.{Response, Routes}
import com.alpha.service.*
import com.alpha.config.*
import com.alpha.provider.*
import com.alpha.dto.*
import com.alpha.domain.model.*
import com.alpha.domain.model.AvailabilitySlot
import pdi.jwt.*
import java.util.UUID

case class AuthResponse(userId: UUID, email: String, name: Option[String], role: String, accessToken: String, refreshToken: String, sessionId: UUID)

object AuthResponse:
  import zio.json.*
  given JsonEncoder[AuthResponse] = DeriveJsonEncoder.gen
  given JsonDecoder[AuthResponse] = DeriveJsonDecoder.gen

object TapirEndpoints:

  private def encodeToken(userId: UUID, email: String, role: String, secret: String, expirySeconds: Long, issuer: String, now: java.time.Instant, tokenType: String): String =
    val content = s"""{"email":"$email","role":"$role","type":"$tokenType"}"""
    Jwt.encode(
      JwtClaim(subject = Some(userId.toString), issuer = Some(issuer), expiration = Some(now.plusSeconds(expirySeconds).getEpochSecond), issuedAt = Some(now.getEpochSecond), content = content),
      secret, JwtAlgorithm.HS256
    )

  private def buildAuthResponse(user: User, session: UserSession, jwt: JwtSettings, tp: TimeProvider): AuthResponse =
    val now = tp.now().toInstant
    AuthResponse(user.id, user.email, user.name, user.role,
      encodeToken(user.id, user.email, user.role, jwt.accessSecret, 900, jwt.issuer, now, "access"),
      encodeToken(user.id, user.email, user.role, jwt.refreshSecret, 604800, jwt.issuer, now, "refresh"),
      session.id)

  private val base = "api" / "v1"
  private val interp = ZioHttpInterpreter()

  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  private val allEndpointsForSwagger: List[ZServerEndpoint[Any, Any]] = List(
    endpoint.post.tag("Auth").summary("Register").in(base / "auth" / "register").in(jsonBody[RegisterUserRequest]).out(jsonBody[AuthResponse]).errorOut(stringBody).zServerLogic[Any](_ => ZIO.succeed(throw new Exception("Not implemented"))),
    endpoint.get.tag("Health").summary("Health check").in(base / "health").out(stringBody).zServerLogic[Any](_ => ZIO.succeed("OK"))
  )

  val authEndpoints: List[ZServerEndpoint[AuthService & AppConfig & TimeProvider, Any]] = List(
    endpoint.post.tag("Auth").summary("Register").in(base / "auth" / "register")
      .in(jsonBody[RegisterUserRequest]).out(jsonBody[AuthResponse]).errorOut(stringBody)
      .zServerLogic { req =>
        (for
          user <- ZIO.serviceWithZIO[AuthService](_.register(req))
          session <- ZIO.serviceWithZIO[AuthService](_.login(LoginUserRequest(req.email, req.password), None, None)).map(_._2)
          config <- ZIO.service[AppConfig]
          tp <- ZIO.service[TimeProvider]
        yield buildAuthResponse(user, session, config.jwt, tp)).mapError(_.getMessage)
      },
    endpoint.post.tag("Auth").summary("Login").in(base / "auth" / "login")
      .in(jsonBody[LoginUserRequest]).out(jsonBody[AuthResponse]).errorOut(stringBody)
      .zServerLogic { req =>
        (for
          (user, session) <- ZIO.serviceWithZIO[AuthService](_.login(req, None, None))
          config <- ZIO.service[AppConfig]; tp <- ZIO.service[TimeProvider]
        yield buildAuthResponse(user, session, config.jwt, tp)).mapError(_.getMessage)
      },
    endpoint.post.tag("Auth").summary("Refresh token").in(base / "auth" / "refresh")
      .in(jsonBody[RefreshTokenRequest]).out(jsonBody[Map[String, String]]).errorOut(stringBody)
      .zServerLogic { req => ZIO.serviceWithZIO[AuthService](_.refreshToken(req)).map { case (a, r) => Map("accessToken" -> a, "refreshToken" -> r) }.mapError(_.getMessage) },
    endpoint.post.tag("Auth").summary("Logout").in(base / "auth" / "logout")
      .in(header[UUID]("X-Session-Id")).out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody)
      .zServerLogic { sid => ZIO.serviceWithZIO[AuthService](_.logout(sid)).mapError(_.getMessage) },
    endpoint.post.tag("Auth").summary("Verify email").in(base / "auth" / "verify-email")
      .in(jsonBody[VerifyEmailRequest]).out(stringBody).errorOut(stringBody)
      .zServerLogic { req => ZIO.serviceWithZIO[AuthService](_.verifyEmail(req)).as("Email verified").mapError(_.getMessage) },
    endpoint.post.tag("Auth").summary("Forgot password").in(base / "auth" / "forgot-password")
      .in(jsonBody[ForgotPasswordRequest]).out(stringBody).errorOut(stringBody)
      .zServerLogic { req => ZIO.serviceWithZIO[AuthService](_.forgotPassword(req)).as("Reset email sent").mapError(_.getMessage) },
    endpoint.post.tag("Auth").summary("Reset password").in(base / "auth" / "reset-password")
      .in(jsonBody[ResetPasswordRequest]).out(stringBody).errorOut(stringBody)
      .zServerLogic { req => ZIO.serviceWithZIO[AuthService](_.resetPassword(req)).as("Password reset").mapError(_.getMessage) },
    endpoint.post.tag("Auth").summary("Change password").in(base / "auth" / "change-password")
      .in(header[UUID]("X-User-Id")).in(jsonBody[ChangePasswordRequest]).out(stringBody).errorOut(stringBody)
      .zServerLogic { case (uid, req) => ZIO.serviceWithZIO[AuthService](_.changePassword(uid, req)).as("Password changed").mapError(_.getMessage) },
    endpoint.get.tag("Auth").summary("Get me").in(base / "auth" / "me")
      .in(header[UUID]("X-User-Id")).out(jsonBody[User]).errorOut(stringBody)
      .zServerLogic { uid => ZIO.serviceWithZIO[AuthService](_.getUser(uid)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage) },
    endpoint.put.tag("Auth").summary("Update me").in(base / "auth" / "me")
      .in(header[UUID]("X-User-Id")).in(jsonBody[UpdateProfileRequest]).out(jsonBody[User]).errorOut(stringBody)
      .zServerLogic { case (uid, req) => ZIO.serviceWithZIO[AuthService](_.updateProfile(uid, req)).mapError(_.getMessage) }
  )

  val categoryEndpoints: List[ZServerEndpoint[CategoryService, Any]] = List(
    endpoint.get.tag("Categories").summary("List categories").in(base / "categories")
      .out(jsonBody[List[Category]]).errorOut(stringBody).zServerLogic { _ => ZIO.serviceWithZIO[CategoryService](_.getAllCategories).mapError(_.getMessage) },
    endpoint.get.tag("Categories").summary("Get category").in(base / "categories" / path[UUID]("id"))
      .out(jsonBody[Category]).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[CategoryService](_.getCategory(id)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage) },
    endpoint.post.tag("Categories").summary("Create category").in(base / "categories")
      .in(jsonBody[CreateCategoryRequest]).out(jsonBody[Category]).errorOut(stringBody).zServerLogic { req => ZIO.serviceWithZIO[CategoryService](_.createCategory(req)).mapError(_.getMessage) },
    endpoint.put.tag("Categories").summary("Update category").in(base / "categories" / path[UUID]("id"))
      .in(jsonBody[UpdateCategoryRequest]).out(jsonBody[Category]).errorOut(stringBody).zServerLogic { case (id, req) => ZIO.serviceWithZIO[CategoryService](_.updateCategory(id, req)).mapError(_.getMessage) },
    endpoint.delete.tag("Categories").summary("Delete category").in(base / "categories" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[CategoryService](_.deleteCategory(id)).mapError(_.getMessage) }
  )

  val regionEndpoints: List[ZServerEndpoint[RegionService, Any]] = List(
    endpoint.get.tag("Regions").summary("List regions").in(base / "regions")
      .out(jsonBody[List[Region]]).errorOut(stringBody).zServerLogic { _ => ZIO.serviceWithZIO[RegionService](_.getAllRegions).mapError(_.getMessage) },
    endpoint.get.tag("Regions").summary("Get region").in(base / "regions" / path[UUID]("id"))
      .out(jsonBody[Region]).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[RegionService](_.getRegionById(id)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage) },
    endpoint.get.tag("Regions").summary("Get region by code").in(base / "regions" / "code" / path[String]("code"))
      .out(jsonBody[Region]).errorOut(stringBody).zServerLogic { code => ZIO.serviceWithZIO[RegionService](_.getRegionByCode(code)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage) },
    endpoint.get.tag("Regions").summary("Search regions").in(base / "regions" / "search")
      .in(query[String]("q")).out(jsonBody[List[Region]]).errorOut(stringBody).zServerLogic { q => ZIO.serviceWithZIO[RegionService](_.searchRegions(q)).mapError(_.getMessage) },
    endpoint.post.tag("Regions").summary("Create region").in(base / "regions")
      .in(jsonBody[CreateRegionRequest]).out(jsonBody[Region]).errorOut(stringBody).zServerLogic { req => ZIO.serviceWithZIO[RegionService](_.createRegion(req)).mapError(_.getMessage) },
    endpoint.put.tag("Regions").summary("Update region").in(base / "regions" / path[UUID]("id"))
      .in(jsonBody[UpdateRegionRequest]).out(jsonBody[Region]).errorOut(stringBody).zServerLogic { case (id, req) => ZIO.serviceWithZIO[RegionService](_.updateRegion(id, req)).mapError(_.getMessage) },
    endpoint.delete.tag("Regions").summary("Delete region").in(base / "regions" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[RegionService](_.deleteRegion(id)).mapError(_.getMessage) }
  )

  val businessEndpoints: List[ZServerEndpoint[BusinessService, Any]] = List(
    endpoint.get.tag("Businesses").summary("List businesses").in(base / "businesses")
      .out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { _ => ZIO.serviceWithZIO[BusinessService](_.getBusinessesByUser(UUID.randomUUID())).mapError(_.getMessage) },
    endpoint.get.tag("Businesses").summary("Get business").in(base / "businesses" / path[UUID]("id"))
      .out(jsonBody[Business]).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[BusinessService](_.getBusiness(id)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage) },
    endpoint.get.tag("Businesses").summary("Get business by slug").in(base / "businesses" / "slug" / path[String]("slug"))
      .out(jsonBody[Business]).errorOut(stringBody).zServerLogic { slug => ZIO.serviceWithZIO[BusinessService](_.getBusinessBySlug(slug)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage) },
    endpoint.get.tag("Businesses").summary("Search businesses").in(base / "businesses" / "search")
      .in(query[String]("q")).out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { q => ZIO.serviceWithZIO[BusinessService](_.searchBusinesses(q)).mapError(_.getMessage) },
    endpoint.get.tag("Businesses").summary("My businesses").in(base / "businesses" / "my-businesses")
      .in(header[UUID]("X-User-Id")).out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { uid => ZIO.serviceWithZIO[BusinessService](_.getBusinessesByUser(uid)).mapError(_.getMessage) },
    endpoint.get.tag("Businesses").summary("By region").in(base / "businesses" / "region" / path[UUID]("regionId"))
      .out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { rid => ZIO.serviceWithZIO[BusinessService](_.getBusinessesByRegion(rid)).mapError(_.getMessage) },
    endpoint.get.tag("Businesses").summary("By category").in(base / "businesses" / "category" / path[UUID]("categoryId"))
      .out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { cid => ZIO.serviceWithZIO[BusinessService](_.getBusinessesByCategory(cid)).mapError(_.getMessage) },
    endpoint.get.tag("Businesses").summary("Featured").in(base / "businesses" / "featured")
      .in(query[Option[Int]]("limit").default(Some(20))).out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { lim => ZIO.serviceWithZIO[BusinessService](_.getFeaturedBusinesses(lim.getOrElse(20))).mapError(_.getMessage) },
    endpoint.post.tag("Businesses").summary("Create business").in(base / "businesses")
      .in(header[UUID]("X-User-Id")).in(jsonBody[CreateBusinessRequest]).out(jsonBody[Business]).errorOut(stringBody).zServerLogic { case (uid, req) => ZIO.serviceWithZIO[BusinessService](_.createBusiness(uid, req)).mapError(_.getMessage) },
    endpoint.put.tag("Businesses").summary("Update business").in(base / "businesses" / path[UUID]("id"))
      .in(jsonBody[UpdateBusinessRequest]).out(jsonBody[Business]).errorOut(stringBody).zServerLogic { case (id, req) => ZIO.serviceWithZIO[BusinessService](_.updateBusiness(id, req)).mapError(_.getMessage) },
    endpoint.delete.tag("Businesses").summary("Delete business").in(base / "businesses" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[BusinessService](_.deleteBusiness(id)).mapError(_.getMessage) },
    endpoint.post.tag("Businesses").summary("Verify business").in(base / "businesses" / path[UUID]("id") / "verify")
      .out(jsonBody[Business]).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[BusinessService](_.verifyBusiness(id)).mapError(_.getMessage) }
  )

  val appointmentEndpoints: List[ZServerEndpoint[AppointmentService, Any]] = List(
    endpoint.get.tag("Appointments").summary("List appointments").in(base / "appointments")
      .in(query[Option[UUID]]("businessId")).in(query[Option[UUID]]("userId")).out(jsonBody[List[Appointment]]).errorOut(stringBody)
      .zServerLogic { case (bid, uid) =>
        val r: ZIO[AppointmentService, Throwable, List[Appointment]] = (bid, uid) match
          case (Some(b), _) => ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByBusiness(b))
          case (_, Some(u)) => ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByUser(u))
          case _ => ZIO.succeed(List.empty[Appointment])
        r.mapError(_.getMessage)
      },
    endpoint.get.tag("Appointments").summary("Get appointment").in(base / "appointments" / path[UUID]("id"))
      .out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[AppointmentService](_.getAppointment(id)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage) },
    endpoint.get.tag("Appointments").summary("Search appointments").in(base / "appointments" / "search")
      .in(query[Option[UUID]]("businessId")).in(query[Option[UUID]]("userId")).in(query[Option[String]]("status")).in(query[Option[java.time.LocalDate]]("date"))
      .out(jsonBody[List[Appointment]]).errorOut(stringBody).zServerLogic { case (b, u, s, d) => ZIO.serviceWithZIO[AppointmentService](_.searchAppointments(b, u, s, d)).mapError(_.getMessage) },
    endpoint.get.tag("Appointments").summary("By business").in(base / "appointments" / "business" / path[UUID]("businessId"))
      .out(jsonBody[List[Appointment]]).errorOut(stringBody).zServerLogic { bid => ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByBusiness(bid)).mapError(_.getMessage) },
    endpoint.get.tag("Appointments").summary("Availability").in(base / "appointments" / "business" / path[UUID]("businessId") / "availability")
      .in(query[java.time.LocalDate]("date")).in(query[Option[UUID]]("serviceId")).out(jsonBody[List[AvailabilitySlot]]).errorOut(stringBody)
      .zServerLogic { case (bid, dt, sid) => ZIO.serviceWithZIO[AppointmentService](_.getAvailability(bid, dt, sid)).mapError(_.getMessage) },
    endpoint.post.tag("Appointments").summary("Create appointment").in(base / "appointments")
      .in(jsonBody[CreateAppointmentRequest]).out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic { req => ZIO.serviceWithZIO[AppointmentService](_.createAppointment(req)).mapError(_.getMessage) },
    endpoint.put.tag("Appointments").summary("Update appointment").in(base / "appointments" / path[UUID]("id"))
      .in(jsonBody[UpdateAppointmentRequest]).out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic { case (id, req) => ZIO.serviceWithZIO[AppointmentService](_.updateAppointment(id, req)).mapError(_.getMessage) },
    endpoint.post.tag("Appointments").summary("Cancel appointment").in(base / "appointments" / path[UUID]("id") / "cancel")
      .in(jsonBody[CancelAppointmentRequest]).out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic { case (id, req) => ZIO.serviceWithZIO[AppointmentService](_.cancelAppointment(id, req)).mapError(_.getMessage) }
  )

  val reviewEndpoints: List[ZServerEndpoint[ReviewService, Any]] = List(
    endpoint.get.tag("Reviews").summary("Get review").in(base / "reviews" / path[UUID]("id"))
      .out(jsonBody[Review]).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[ReviewService](_.getReview(id)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage) },
    endpoint.get.tag("Reviews").summary("By business").in(base / "reviews" / "business" / path[UUID]("businessId"))
      .out(jsonBody[List[Review]]).errorOut(stringBody).zServerLogic { bid => ZIO.serviceWithZIO[ReviewService](_.getReviewsByBusiness(bid)).mapError(_.getMessage) },
    endpoint.get.tag("Reviews").summary("Approved by business").in(base / "reviews" / "business" / path[UUID]("businessId") / "approved")
      .out(jsonBody[List[Review]]).errorOut(stringBody).zServerLogic { bid => ZIO.serviceWithZIO[ReviewService](_.getApprovedReviewsByBusiness(bid)).mapError(_.getMessage) },
    endpoint.get.tag("Reviews").summary("By user").in(base / "reviews" / "user" / path[UUID]("userId"))
      .out(jsonBody[List[Review]]).errorOut(stringBody).zServerLogic { uid => ZIO.serviceWithZIO[ReviewService](_.getReviewsByUser(uid)).mapError(_.getMessage) },
    endpoint.get.tag("Reviews").summary("Average rating").in(base / "reviews" / "business" / path[UUID]("businessId") / "average-rating")
      .out(jsonBody[Double]).errorOut(stringBody).zServerLogic { bid => ZIO.serviceWithZIO[ReviewService](_.getAverageRating(bid)).map(_.getOrElse(0.0)).mapError(_.getMessage) },
    endpoint.post.tag("Reviews").summary("Create review").in(base / "reviews")
      .in(jsonBody[CreateReviewRequest]).out(jsonBody[Review]).errorOut(stringBody).zServerLogic { req => ZIO.serviceWithZIO[ReviewService](_.createReview(req)).mapError(_.getMessage) },
    endpoint.post.tag("Reviews").summary("Approve review").in(base / "reviews" / path[UUID]("id") / "approve")
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[ReviewService](_.approveReview(id)).mapError(_.getMessage) },
    endpoint.delete.tag("Reviews").summary("Delete review").in(base / "reviews" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[ReviewService](_.deleteReview(id)).mapError(_.getMessage) }
  )

  val serviceEndpoints: List[ZServerEndpoint[com.alpha.service.ServiceService, Any]] = List(
    endpoint.get.tag("Services").summary("Get service").in(base / "services" / path[UUID]("id"))
      .out(jsonBody[com.alpha.domain.model.Service]).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getService(id)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage) },
    endpoint.get.tag("Services").summary("By business").in(base / "services" / "business" / path[UUID]("businessId"))
      .out(jsonBody[List[com.alpha.domain.model.Service]]).errorOut(stringBody).zServerLogic { bid => ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getServicesByBusiness(bid)).mapError(_.getMessage) },
    endpoint.get.tag("Services").summary("Active by business").in(base / "services" / "business" / path[UUID]("businessId") / "active")
      .out(jsonBody[List[com.alpha.domain.model.Service]]).errorOut(stringBody).zServerLogic { bid => ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getActiveServicesByBusiness(bid)).mapError(_.getMessage) },
    endpoint.post.tag("Services").summary("Create service").in(base / "services" / "business" / path[UUID]("businessId"))
      .in(jsonBody[CreateServiceRequest]).out(jsonBody[com.alpha.domain.model.Service]).errorOut(stringBody).zServerLogic { case (bid, req) => ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.createService(bid, req)).mapError(_.getMessage) },
    endpoint.put.tag("Services").summary("Update service").in(base / "services" / path[UUID]("id"))
      .in(jsonBody[UpdateServiceRequest]).out(jsonBody[com.alpha.domain.model.Service]).errorOut(stringBody).zServerLogic { case (id, req) => ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.updateService(id, req)).mapError(_.getMessage) },
    endpoint.delete.tag("Services").summary("Delete service").in(base / "services" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.deleteService(id)).mapError(_.getMessage) }
  )

  val businessHoursEndpoints: List[ZServerEndpoint[BusinessHoursService, Any]] = List(
    endpoint.get.tag("Business Hours").summary("Get hours").in(base / "business-hours" / path[UUID]("id"))
      .out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[BusinessHoursService](_.getHours(id)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage) },
    endpoint.get.tag("Business Hours").summary("By business").in(base / "business-hours" / "business" / path[UUID]("businessId"))
      .out(jsonBody[List[BusinessHours]]).errorOut(stringBody).zServerLogic { bid => ZIO.serviceWithZIO[BusinessHoursService](_.getHoursByBusiness(bid)).mapError(_.getMessage) },
    endpoint.get.tag("Business Hours").summary("By business and day").in(base / "business-hours" / "business" / path[UUID]("businessId") / "day" / path[Int]("dayOfWeek"))
      .out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic { case (bid, dow) => ZIO.serviceWithZIO[BusinessHoursService](_.getHoursByBusinessAndDay(bid, dow)).flatMap(ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage) },
    endpoint.post.tag("Business Hours").summary("Create hours").in(base / "business-hours")
      .in(jsonBody[CreateBusinessHoursRequest]).out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic { req => ZIO.serviceWithZIO[BusinessHoursService](_.createHours(req)).mapError(_.getMessage) },
    endpoint.put.tag("Business Hours").summary("Update hours").in(base / "business-hours" / path[UUID]("id"))
      .in(jsonBody[UpdateBusinessHoursRequest]).out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic { case (id, req) => ZIO.serviceWithZIO[BusinessHoursService](_.updateHours(id, req)).mapError(_.getMessage) },
    endpoint.delete.tag("Business Hours").summary("Delete hours").in(base / "business-hours" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id => ZIO.serviceWithZIO[BusinessHoursService](_.deleteHours(id)).mapError(_.getMessage) },
    endpoint.delete.tag("Business Hours").summary("Delete all by business").in(base / "business-hours" / "business" / path[UUID]("businessId"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { bid => ZIO.serviceWithZIO[BusinessHoursService](_.deleteAllByBusiness(bid)).mapError(_.getMessage) }
  )

  val healthEndpoints: List[ZServerEndpoint[Any, Any]] = List(
    endpoint.get.tag("Health").summary("Health check").in(base / "health").out(stringBody).zServerLogic[Any](_ => ZIO.succeed("OK"))
  )

  val authRoutes: URIO[AuthService & AppConfig & TimeProvider, Routes[Any, Response]] = toRoutes(authEndpoints)
  val categoryRoutes: URIO[CategoryService, Routes[Any, Response]] = toRoutes(categoryEndpoints)
  val regionRoutes: URIO[RegionService, Routes[Any, Response]] = toRoutes(regionEndpoints)
  val businessRoutes: URIO[BusinessService, Routes[Any, Response]] = toRoutes(businessEndpoints)
  val appointmentRoutes: URIO[AppointmentService, Routes[Any, Response]] = toRoutes(appointmentEndpoints)
  val reviewRoutes: URIO[ReviewService, Routes[Any, Response]] = toRoutes(reviewEndpoints)
  val serviceRoutes: URIO[com.alpha.service.ServiceService, Routes[Any, Response]] = toRoutes(serviceEndpoints)
  val businessHoursRoutes: URIO[BusinessHoursService, Routes[Any, Response]] = toRoutes(businessHoursEndpoints)
  val healthRoutes: URIO[Any, Routes[Any, Response]] = toRoutes(healthEndpoints)

  val swaggerRoutes: Routes[Any, Response] =
    interp.toHttp(SwaggerInterpreter().fromServerEndpoints(allEndpointsForSwagger, "Alpha API", "1.0.0"))

  val allRoutes: URIO[
    AuthService & AppConfig & TimeProvider &
    CategoryService & RegionService & BusinessService &
    AppointmentService & ReviewService &
    com.alpha.service.ServiceService & BusinessHoursService,
    Routes[Any, Response]
  ] =
    for
      auth <- authRoutes; cat <- categoryRoutes; reg <- regionRoutes; biz <- businessRoutes
      appt <- appointmentRoutes; rev <- reviewRoutes; svc <- serviceRoutes; hrs <- businessHoursRoutes; health <- healthRoutes
    yield auth ++ cat ++ reg ++ biz ++ appt ++ rev ++ svc ++ hrs ++ health ++ swaggerRoutes
