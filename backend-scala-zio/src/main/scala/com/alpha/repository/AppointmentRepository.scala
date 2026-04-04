package com.alpha.repository

import zio.*
import com.alpha.domain.model.*
import java.util.UUID
import java.time.LocalDate
import io.getquill.*
import io.getquill.extras.LocalDateOps

trait AppointmentRepository:
  def findById(id: UUID): Task[Option[Appointment]]
  def findByBusinessId(businessId: UUID): Task[List[Appointment]]
  def findByCustomerId(userId: UUID): Task[List[Appointment]]
  def findByStatus(status: String): Task[List[Appointment]]
  def findByBusinessIdAndDate(businessId: UUID, date: LocalDate): Task[List[Appointment]]
  def findByBusinessIdAndDateAndStatus(businessId: UUID, date: LocalDate, status: String): Task[List[Appointment]]
  def findByBusinessIdAndDateRange(businessId: UUID, start: LocalDate, end: LocalDate): Task[List[Appointment]]
  def create(appointment: Appointment): Task[UUID]
  def update(appointment: Appointment): Task[Int]
  def updateStatus(id: UUID, status: String): Task[Int]
  def delete(id: UUID): Task[Int]

object AppointmentRepository:
  val layer: ZLayer[PostgresCtx, Nothing, AppointmentRepository] = 
    ZLayer.fromFunction(new AppointmentRepositoryImpl(_))

class AppointmentRepositoryImpl(ctx: PostgresCtx) extends AppointmentRepository:

  import ctx.*

  override def findById(id: UUID): Task[Option[Appointment]] = 
    ZIO.attempt:
      run(query[Appointment].filter(_.id == lift(id))).headOption

  override def findByBusinessId(businessId: UUID): Task[List[Appointment]] = 
    ZIO.attempt:
      run(query[Appointment].filter(_.businessId == lift(businessId))).toList

  override def findByCustomerId(userId: UUID): Task[List[Appointment]] = 
    ZIO.attempt:
      run(query[Appointment].filter(_.userId.exists(_ == lift(userId)))).toList

  override def findByStatus(status: String): Task[List[Appointment]] = 
    ZIO.attempt:
      run(query[Appointment].filter(_.status == lift(status))).toList

  override def findByBusinessIdAndDate(businessId: UUID, date: LocalDate): Task[List[Appointment]] = 
    ZIO.attempt:
      run(query[Appointment]
        .filter(a => a.businessId == lift(businessId) && a.appointmentDate == lift(date))
      ).toList

  override def findByBusinessIdAndDateAndStatus(businessId: UUID, date: LocalDate, status: String): Task[List[Appointment]] = 
    ZIO.attempt:
      run(query[Appointment]
        .filter(a => a.businessId == lift(businessId) && a.appointmentDate == lift(date) && a.status == lift(status))
      ).toList

  override def findByBusinessIdAndDateRange(businessId: UUID, start: LocalDate, end: LocalDate): Task[List[Appointment]] = 
    ZIO.attempt:
      run(query[Appointment]
        .filter(_.businessId == lift(businessId))
        .filter(a => a.appointmentDate >= lift(start))
        .filter(a => a.appointmentDate <= lift(end))
      ).toList

  override def create(appointment: Appointment): Task[UUID] = 
    ZIO.attempt:
      run(query[Appointment].insertValue(lift(appointment)))
      appointment.id

  override def update(appointment: Appointment): Task[Int] = 
    ZIO.attempt:
      run(query[Appointment]
        .filter(_.id == lift(appointment.id))
        .updateValue(lift(appointment)))
      1

  override def updateStatus(id: UUID, status: String): Task[Int] = 
    ZIO.attempt:
      run(query[Appointment]
        .filter(_.id == lift(id))
        .update(_.status -> lift(status)))
      1

  override def delete(id: UUID): Task[Int] = 
    ZIO.attempt:
      run(query[Appointment].filter(_.id == lift(id)).delete)
      1
