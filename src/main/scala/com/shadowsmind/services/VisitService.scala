package com.shadowsmind.services

import java.sql.Timestamp

import akka.actor.ActorSystem
import com.shadowsmind.api.directives.VisitsRequestParams
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

  def find(userId: Long, params: VisitsRequestParams): ServiceResult[Seq[Visit]] = {
    UserRepository.exists(userId).flatMap {
      case true ⇒
        val findResult =
          if (params.fromDate.isEmpty && params.toDate.isEmpty && params.country.isEmpty && params.toDistance.isEmpty) {
            VisitRepository.findByUser(userId)
          } else if (params.country.isEmpty && params.toDistance.isEmpty) {
            VisitRepository.findByUserAndDate(userId, params.fromDate, params.toDate)
          } else {
            LocationRepository.findIdsByGeo(params.country, params.toDistance).flatMap {
              case Nil ⇒ async(Seq.empty[Visit])

              case locationsIds ⇒
                VisitRepository.findByUserAndLocationsAndDate(userId, locationsIds, params.fromDate, params.toDate)
            }
          }

        findResult.toResult

      case false ⇒ async(error(404))
    }
  }

}
