package com.alpha.controller

import sttp.tapir.ztapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.*
import zio.http.{Response, Routes}
import com.alpha.service.*
import com.alpha.dto.*
import com.alpha.domain.model.*
import java.util.UUID

object CategoryEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[CategoryService, Any]] = List(
    endpoint.get.tag("Categories").summary("List categories").in(base / "categories")
      .out(jsonBody[List[Category]]).errorOut(stringBody).zServerLogic { _ =>
        ZIO.serviceWithZIO[CategoryService](_.getAllCategories).mapError(_.getMessage)
      },
    endpoint.get.tag("Categories").summary("Get category").in(base / "categories" / path[UUID]("id"))
      .out(jsonBody[Category]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[CategoryService](_.getCategory(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.post.tag("Categories").summary("Create category").in(base / "categories")
      .in(jsonBody[CreateCategoryRequest]).out(jsonBody[Category]).errorOut(stringBody).zServerLogic { req =>
        ZIO.serviceWithZIO[CategoryService](_.createCategory(req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Categories").summary("Update category").in(base / "categories" / path[UUID]("id"))
      .in(jsonBody[UpdateCategoryRequest]).out(jsonBody[Category]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[CategoryService](_.updateCategory(id, req)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Categories").summary("Delete category").in(base / "categories" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[CategoryService](_.deleteCategory(id)).mapError(_.getMessage)
      }
  )

  val routes: URIO[CategoryService, Routes[Any, Response]] = toRoutes(endpoints)

object RegionEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[RegionService, Any]] = List(
    endpoint.get.tag("Regions").summary("List regions").in(base / "regions")
      .out(jsonBody[List[Region]]).errorOut(stringBody).zServerLogic { _ =>
        ZIO.serviceWithZIO[RegionService](_.getAllRegions).mapError(_.getMessage)
      },
    endpoint.get.tag("Regions").summary("Get region").in(base / "regions" / path[UUID]("id"))
      .out(jsonBody[Region]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[RegionService](_.getRegionById(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Regions").summary("Get region by code").in(base / "regions" / "code" / path[String]("code"))
      .out(jsonBody[Region]).errorOut(stringBody).zServerLogic { code =>
        ZIO.serviceWithZIO[RegionService](_.getRegionByCode(code)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Regions").summary("Search regions").in(base / "regions" / "search")
      .in(query[String]("q")).out(jsonBody[List[Region]]).errorOut(stringBody).zServerLogic { q =>
        ZIO.serviceWithZIO[RegionService](_.searchRegions(q)).mapError(_.getMessage)
      },
    endpoint.post.tag("Regions").summary("Create region").in(base / "regions")
      .in(jsonBody[CreateRegionRequest]).out(jsonBody[Region]).errorOut(stringBody).zServerLogic { req =>
        ZIO.serviceWithZIO[RegionService](_.createRegion(req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Regions").summary("Update region").in(base / "regions" / path[UUID]("id"))
      .in(jsonBody[UpdateRegionRequest]).out(jsonBody[Region]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[RegionService](_.updateRegion(id, req)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Regions").summary("Delete region").in(base / "regions" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[RegionService](_.deleteRegion(id)).mapError(_.getMessage)
      }
  )

  val routes: URIO[RegionService, Routes[Any, Response]] = toRoutes(endpoints)

object BusinessEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[BusinessService, Any]] = List(
    endpoint.get.tag("Businesses").summary("List businesses").in(base / "businesses")
      .out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { _ =>
        ZIO.serviceWithZIO[BusinessService](_.getBusinessesByUser(UUID.randomUUID())).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("Get business").in(base / "businesses" / path[UUID]("id"))
      .out(jsonBody[Business]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[BusinessService](_.getBusiness(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("Get business by slug").in(base / "businesses" / "slug" / path[String](
      "slug"))
      .out(jsonBody[Business]).errorOut(stringBody).zServerLogic { slug =>
        ZIO.serviceWithZIO[BusinessService](_.getBusinessBySlug(slug)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("Search businesses").in(base / "businesses" / "search")
      .in(query[String]("q")).out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { q =>
        ZIO.serviceWithZIO[BusinessService](_.searchBusinesses(q)).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("My businesses").in(base / "businesses" / "my-businesses")
      .in(header[UUID]("X-User-Id")).out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { uid =>
        ZIO.serviceWithZIO[BusinessService](_.getBusinessesByUser(uid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("By region").in(base / "businesses" / "region" / path[UUID]("regionId"))
      .out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { rid =>
        ZIO.serviceWithZIO[BusinessService](_.getBusinessesByRegion(rid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("By category").in(base / "businesses" / "category" / path[UUID](
      "categoryId"))
      .out(jsonBody[List[Business]]).errorOut(stringBody).zServerLogic { cid =>
        ZIO.serviceWithZIO[BusinessService](_.getBusinessesByCategory(cid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Businesses").summary("Featured").in(base / "businesses" / "featured")
      .in(query[Option[Int]]("limit").default(Some(20))).out(jsonBody[List[Business]]).errorOut(
        stringBody).zServerLogic { lim =>
        ZIO.serviceWithZIO[BusinessService](_.getFeaturedBusinesses(lim.getOrElse(20))).mapError(_.getMessage)
      },
    endpoint.post.tag("Businesses").summary("Create business").in(base / "businesses")
      .in(header[UUID]("X-User-Id")).in(jsonBody[CreateBusinessRequest]).out(jsonBody[Business]).errorOut(
        stringBody).zServerLogic {
        case (uid, req) => ZIO.serviceWithZIO[BusinessService](_.createBusiness(uid, req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Businesses").summary("Update business").in(base / "businesses" / path[UUID]("id"))
      .in(jsonBody[UpdateBusinessRequest]).out(jsonBody[Business]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[BusinessService](_.updateBusiness(id, req)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Businesses").summary("Delete business").in(base / "businesses" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[BusinessService](_.deleteBusiness(id)).mapError(_.getMessage)
      },
    endpoint.post.tag("Businesses").summary("Verify business").in(base / "businesses" / path[UUID]("id") / "verify")
      .out(jsonBody[Business]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[BusinessService](_.verifyBusiness(id)).mapError(_.getMessage)
      }
  )

  val routes: URIO[BusinessService, Routes[Any, Response]] = toRoutes(endpoints)

object AppointmentEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[AppointmentService, Any]] = List(
    endpoint.get.tag("Appointments").summary("List appointments").in(base / "appointments")
      .in(query[Option[UUID]]("businessId")).in(query[Option[UUID]]("userId")).out(
        jsonBody[List[Appointment]]).errorOut(stringBody)
      .zServerLogic {
        case (bid, uid) =>
          val r: ZIO[AppointmentService, Throwable, List[Appointment]] = (bid, uid) match
            case (Some(b), _) => ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByBusiness(b))
            case (_, Some(u)) => ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByUser(u))
            case _            => ZIO.succeed(List.empty[Appointment])
          r.mapError(_.getMessage)
      },
    endpoint.get.tag("Appointments").summary("Get appointment").in(base / "appointments" / path[UUID]("id"))
      .out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[AppointmentService](_.getAppointment(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Appointments").summary("Search appointments").in(base / "appointments" / "search")
      .in(query[Option[UUID]]("businessId")).in(query[Option[UUID]]("userId")).in(query[Option[String]]("status")).in(
        query[Option[java.time.LocalDate]]("date"))
      .out(jsonBody[List[Appointment]]).errorOut(stringBody).zServerLogic {
        case (b, u, s, d) =>
          ZIO.serviceWithZIO[AppointmentService](_.searchAppointments(b, u, s, d)).mapError(_.getMessage)
      },
    endpoint.get.tag("Appointments").summary("By business").in(base / "appointments" / "business" / path[UUID](
      "businessId"))
      .out(jsonBody[List[Appointment]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[AppointmentService](_.getAppointmentsByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Appointments").summary("Availability").in(base / "appointments" / "business" / path[UUID](
      "businessId") / "availability")
      .in(query[java.time.LocalDate]("date")).in(query[Option[UUID]]("serviceId")).out(
        jsonBody[List[AvailabilitySlot]]).errorOut(stringBody)
      .zServerLogic {
        case (bid, dt, sid) =>
          ZIO.serviceWithZIO[AppointmentService](_.getAvailability(bid, dt, sid)).mapError(_.getMessage)
      },
    endpoint.post.tag("Appointments").summary("Create appointment").in(base / "appointments")
      .in(jsonBody[CreateAppointmentRequest]).out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic { req =>
        ZIO.serviceWithZIO[AppointmentService](_.createAppointment(req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Appointments").summary("Update appointment").in(base / "appointments" / path[UUID]("id"))
      .in(jsonBody[UpdateAppointmentRequest]).out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[AppointmentService](_.updateAppointment(id, req)).mapError(_.getMessage)
      },
    endpoint.post.tag("Appointments").summary("Cancel appointment").in(base / "appointments" / path[UUID](
      "id") / "cancel")
      .in(jsonBody[CancelAppointmentRequest]).out(jsonBody[Appointment]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[AppointmentService](_.cancelAppointment(id, req)).mapError(_.getMessage)
      }
  )

  val routes: URIO[AppointmentService, Routes[Any, Response]] = toRoutes(endpoints)

object ReviewEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[ReviewService, Any]] = List(
    endpoint.get.tag("Reviews").summary("Get review").in(base / "reviews" / path[UUID]("id"))
      .out(jsonBody[Review]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[ReviewService](_.getReview(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Reviews").summary("By business").in(base / "reviews" / "business" / path[UUID]("businessId"))
      .out(jsonBody[List[Review]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[ReviewService](_.getReviewsByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Reviews").summary("Approved by business").in(base / "reviews" / "business" / path[UUID](
      "businessId") / "approved")
      .out(jsonBody[List[Review]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[ReviewService](_.getApprovedReviewsByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Reviews").summary("By user").in(base / "reviews" / "user" / path[UUID]("userId"))
      .out(jsonBody[List[Review]]).errorOut(stringBody).zServerLogic { uid =>
        ZIO.serviceWithZIO[ReviewService](_.getReviewsByUser(uid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Reviews").summary("Average rating").in(base / "reviews" / "business" / path[UUID](
      "businessId") / "average-rating")
      .out(jsonBody[Double]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[ReviewService](_.getAverageRating(bid)).map(_.getOrElse(0.0)).mapError(_.getMessage)
      },
    endpoint.post.tag("Reviews").summary("Create review").in(base / "reviews")
      .in(jsonBody[CreateReviewRequest]).out(jsonBody[Review]).errorOut(stringBody).zServerLogic { req =>
        ZIO.serviceWithZIO[ReviewService](_.createReview(req)).mapError(_.getMessage)
      },
    endpoint.post.tag("Reviews").summary("Approve review").in(base / "reviews" / path[UUID]("id") / "approve")
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[ReviewService](_.approveReview(id)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Reviews").summary("Delete review").in(base / "reviews" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[ReviewService](_.deleteReview(id)).mapError(_.getMessage)
      }
  )

  val routes: URIO[ReviewService, Routes[Any, Response]] = toRoutes(endpoints)

object ServiceEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[com.alpha.service.ServiceService, Any]] = List(
    endpoint.get.tag("Services").summary("Get service").in(base / "services" / path[UUID]("id"))
      .out(jsonBody[com.alpha.domain.model.Service]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getService(id)).flatMap(ZIO.fromOption(_).orElseFail(
          new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Services").summary("By business").in(base / "services" / "business" / path[UUID]("businessId"))
      .out(jsonBody[List[com.alpha.domain.model.Service]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getServicesByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Services").summary("Active by business").in(base / "services" / "business" / path[UUID](
      "businessId") / "active")
      .out(jsonBody[List[com.alpha.domain.model.Service]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.getActiveServicesByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.post.tag("Services").summary("Create service").in(base / "services" / "business" / path[UUID](
      "businessId"))
      .in(jsonBody[CreateServiceRequest]).out(jsonBody[com.alpha.domain.model.Service]).errorOut(
        stringBody).zServerLogic {
        case (bid, req) =>
          ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.createService(bid, req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Services").summary("Update service").in(base / "services" / path[UUID]("id"))
      .in(jsonBody[UpdateServiceRequest]).out(jsonBody[com.alpha.domain.model.Service]).errorOut(
        stringBody).zServerLogic {
        case (id, req) =>
          ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.updateService(id, req)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Services").summary("Delete service").in(base / "services" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[com.alpha.service.ServiceService](_.deleteService(id)).mapError(_.getMessage)
      }
  )

  val routes: URIO[com.alpha.service.ServiceService, Routes[Any, Response]] = toRoutes(endpoints)

object BusinessHoursEndpoints:
  private val base                                                                                  = "api" / "v1"
  private val interp                                                                                = ZioHttpInterpreter()
  private def toRoutes[R](endpoints: List[ZServerEndpoint[R, Any]]): URIO[R, Routes[Any, Response]] =
    ZIO.succeed(interp.toHttp(endpoints).sandbox.asInstanceOf[Routes[Any, Response]])

  val endpoints: List[ZServerEndpoint[BusinessHoursService, Any]] = List(
    endpoint.get.tag("Business Hours").summary("Get hours").in(base / "business-hours" / path[UUID]("id"))
      .out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[BusinessHoursService](_.getHours(id)).flatMap(
          ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.get.tag("Business Hours").summary("By business").in(base / "business-hours" / "business" / path[UUID](
      "businessId"))
      .out(jsonBody[List[BusinessHours]]).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[BusinessHoursService](_.getHoursByBusiness(bid)).mapError(_.getMessage)
      },
    endpoint.get.tag("Business Hours").summary("By business and day").in(
      base / "business-hours" / "business" / path[UUID]("businessId") / "day" / path[Int]("dayOfWeek"))
      .out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic {
        case (bid, dow) => ZIO.serviceWithZIO[BusinessHoursService](_.getHoursByBusinessAndDay(bid, dow)).flatMap(
            ZIO.fromOption(_).orElseFail(new Exception("Not found"))).mapError(_.getMessage)
      },
    endpoint.post.tag("Business Hours").summary("Create hours").in(base / "business-hours")
      .in(jsonBody[CreateBusinessHoursRequest]).out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic { req =>
        ZIO.serviceWithZIO[BusinessHoursService](_.createHours(req)).mapError(_.getMessage)
      },
    endpoint.put.tag("Business Hours").summary("Update hours").in(base / "business-hours" / path[UUID]("id"))
      .in(jsonBody[UpdateBusinessHoursRequest]).out(jsonBody[BusinessHours]).errorOut(stringBody).zServerLogic {
        case (id, req) => ZIO.serviceWithZIO[BusinessHoursService](_.updateHours(id, req)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Business Hours").summary("Delete hours").in(base / "business-hours" / path[UUID]("id"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { id =>
        ZIO.serviceWithZIO[BusinessHoursService](_.deleteHours(id)).mapError(_.getMessage)
      },
    endpoint.delete.tag("Business Hours").summary("Delete all by business").in(
      base / "business-hours" / "business" / path[UUID]("businessId"))
      .out(statusCode(sttp.model.StatusCode(204))).errorOut(stringBody).zServerLogic { bid =>
        ZIO.serviceWithZIO[BusinessHoursService](_.deleteAllByBusiness(bid)).mapError(_.getMessage)
      }
  )

  val routes: URIO[BusinessHoursService, Routes[Any, Response]] = toRoutes(endpoints)

object HealthEndpoint:
  private val base   = "api" / "v1"
  private val interp = ZioHttpInterpreter()

  val routes: Routes[Any, Response] =
    interp.toHttp(List(
      endpoint.get.tag("Health").summary("Health check").in(base / "health").out(stringBody).zServerLogic[Any](_ =>
        ZIO.succeed("OK"))
    ))
