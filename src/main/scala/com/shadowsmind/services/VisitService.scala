package com.shadowsmind.services

import java.sql.Timestamp

import akka.actor.ActorSystem
import com.shadowsmind.models.{ Visit, VisitUpdateDto }
import com.shadowsmind.persistence.{ LocationRepository, UserRepository, VisitRepository }

import scala.concurrent.ExecutionContextExecutor

class VisitService(
  implicit
  actorSystem: ActorSystem,
  dispatcher:  ExecutionContextExecutor
) {

  def create(visit: Visit): ServiceResult[Unit] = {
    VisitRepository.findOne(visit.id).flatMap {
      case Some(_) ⇒ async(error(400))
      case None    ⇒ VisitRepository.save(visit).mapToUnit
    }
  }

  def update(id: Long, dto: VisitUpdateDto): ServiceResult[Unit] = {
    VisitRepository.findOne(id).flatMap {
      case Some(visit) ⇒
        val updatedVisit = visit.update(dto)
        VisitRepository.update(id, updatedVisit).mapToUnit

      case None ⇒ async(error(404))
    }
  }

  def findOne(id: Long): ServiceResult[Visit] = {
    VisitRepository.findOne(id).map {
      case Some(v) ⇒ success(v)
      case None    ⇒ error(404)
    }
  }

  def find(
    userId:   Long,
    fromDate: Option[Timestamp], toDate: Option[Timestamp],
    country: Option[String], toDistance: Option[Long]
  ): ServiceResult[Seq[Visit]] = {
    UserRepository.exists(userId).flatMap {
      case true ⇒
        val findResult =
          if (fromDate.isEmpty && toDate.isEmpty && country.isEmpty && toDistance.isEmpty) {
            VisitRepository.findByUser(userId)
          } else if (country.isEmpty && toDistance.isEmpty) {
            VisitRepository.findByUserAndDate(userId, fromDate, toDate)
          } else {
            LocationRepository.findIdsByGeo(country, toDistance).flatMap {
              case Nil ⇒ async(Seq.empty[Visit])

              case locationsIds ⇒
                VisitRepository.findByUserAndLocationsAndDate(userId, locationsIds, fromDate, toDate)
            }
          }

        findResult.toResult

      case false ⇒ async(error(404))
    }
  }

}
