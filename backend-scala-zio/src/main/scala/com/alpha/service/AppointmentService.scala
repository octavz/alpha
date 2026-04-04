package com.alpha.service

import zio.*
import com.alpha.repository.*
import com.alpha.domain.enums.*
import com.alpha.domain.model.*
import com.alpha.dto.*
import com.alpha.provider.*
import java.util.UUID
import java.time.OffsetDateTime

trait AppointmentService:
  def getAppointment(id: UUID): Task[Option[Appointment]]
  def getAppointmentsByBusiness(businessId: UUID): Task[List[Appointment]]
  def getAppointmentsByUser(userId: UUID): Task[List[Appointment]]
  def getAppointmentsByDateRange(
    businessId: UUID,
    start: java.time.LocalDate,
    end: java.time.LocalDate): Task[List[Appointment]]
  def searchAppointments(
    businessId: Option[UUID],
    userId: Option[UUID],
    status: Option[String],
    date: Option[java.time.LocalDate]): Task[List[Appointment]]
  def getAvailability(
    businessId: UUID,
    date: java.time.LocalDate,
    serviceId: Option[UUID]): Task[List[AvailabilitySlot]]
  def createAppointment(req: CreateAppointmentRequest): Task[Appointment]
  def updateAppointment(id: UUID, req: UpdateAppointmentRequest): Task[Appointment]
  def cancelAppointment(id: UUID, req: CancelAppointmentRequest): Task[Appointment]

object AppointmentService:
  val layer: ZLayer[AppointmentRepository & TimeProvider & UUIDProvider, Nothing, AppointmentService] =
    ZLayer.fromFunction(new AppointmentServiceImpl(_, _, _))

class AppointmentServiceImpl(
  appointmentRepo: AppointmentRepository,
  timeProvider: TimeProvider,
  uuidProvider: UUIDProvider) extends AppointmentService:

  override def getAppointment(id: UUID): Task[Option[Appointment]] =
    appointmentRepo.findById(id)

  override def getAppointmentsByBusiness(businessId: UUID): Task[List[Appointment]] =
    appointmentRepo.findByBusinessId(businessId)

  override def getAppointmentsByUser(userId: UUID): Task[List[Appointment]] =
    appointmentRepo.findByCustomerId(userId)

  override def getAppointmentsByDateRange(
    businessId: UUID,
    start: java.time.LocalDate,
    end: java.time.LocalDate): Task[List[Appointment]] =
    appointmentRepo.findByBusinessIdAndDateRange(businessId, start, end)

  override def searchAppointments(
    businessId: Option[UUID],
    userId: Option[UUID],
    status: Option[String],
    date: Option[java.time.LocalDate]): Task[List[Appointment]] =
    val appointmentStatus = status.flatMap(s => AppointmentStatus.values.find(_.value == s))
    (businessId, userId, appointmentStatus, date) match
      case (Some(bId), _, Some(s), Some(d)) => appointmentRepo.findByBusinessIdAndDateAndStatus(bId, d, s)
      case (Some(bId), _, _, Some(d))       => appointmentRepo.findByBusinessIdAndDate(bId, d)
      case (Some(bId), _, Some(s), _)       => appointmentRepo.findByStatus(s)
      case (Some(bId), _, _, _)             => appointmentRepo.findByBusinessId(bId)
      case (_, Some(uId), _, _)             => appointmentRepo.findByCustomerId(uId)
      case (_, _, Some(s), _)               => appointmentRepo.findByStatus(s)
      case _                                => ZIO.succeed(List.empty[Appointment])

  override def getAvailability(
    businessId: UUID,
    date: java.time.LocalDate,
    serviceId: Option[UUID]): Task[List[AvailabilitySlot]] =
    for
      appointments <- appointmentRepo.findByBusinessIdAndDate(businessId, date)
      bookedSlots   = appointments
                        .filter(a => serviceId.isEmpty || a.serviceId == serviceId)
                        .filter(_.status != AppointmentStatus.CANCELLED.value)
                        .map(a => AvailabilitySlot(a.startTime, a.endTime, a.servicePointNumber, isAvailable = false))
    yield bookedSlots

  override def createAppointment(req: CreateAppointmentRequest): Task[Appointment] =
    val appointment = Appointment(
      id = uuidProvider.randomUUID(),
      businessId = req.businessId,
      userId = req.userId,
      serviceId = req.serviceId,
      appointmentDate = req.appointmentDate,
      startTime = req.startTime,
      endTime = req.endTime,
      servicePointNumber = req.servicePointNumber,
      customerName = req.customerName,
      customerEmail = req.customerEmail,
      customerPhone = req.customerPhone,
      customerNotes = req.customerNotes,
      status = AppointmentStatus.PENDING.value,
      cancelledAt = None,
      cancelledReason = None,
      createdAt = timeProvider.now(),
      updatedAt = None
    )
    for
      id <- appointmentRepo.create(appointment)
    yield appointment.copy(id = id)

  override def updateAppointment(id: UUID, req: UpdateAppointmentRequest): Task[Appointment] =
    for
      appointmentOpt <- appointmentRepo.findById(id)
      appointment    <- ZIO.fromOption(appointmentOpt).orElseFail(new Exception("Appointment not found"))
      updated         = appointment.copy(
                          status =
                            req.status.flatMap(s => AppointmentStatus.values.find(_.value == s)).map(_.value).getOrElse(
                              appointment.status),
                          customerNotes = req.customerNotes.orElse(appointment.customerNotes),
                          updatedAt = Some(timeProvider.now())
                        )
      _              <- appointmentRepo.update(updated)
    yield updated

  override def cancelAppointment(id: UUID, req: CancelAppointmentRequest): Task[Appointment] =
    for
      appointmentOpt <- appointmentRepo.findById(id)
      appointment    <- ZIO.fromOption(appointmentOpt).orElseFail(new Exception("Appointment not found"))
      updated         = appointment.copy(
                          status = AppointmentStatus.CANCELLED.value,
                          cancelledAt = Some(timeProvider.now()),
                          cancelledReason = req.reason,
                          updatedAt = Some(timeProvider.now())
                        )
      _              <- appointmentRepo.update(updated)
    yield updated
