package com.shadowsmind.services

import akka.actor.ActorSystem
import com.shadowsmind.api.directives.VisitsRequestParams
import com.shadowsmind.models.{ UserVisit, Visit, VisitUpdateDto }
import com.shadowsmind.persistence.{ LocationRepository, UserRepository, VisitRepository }

import scala.concurrent.ExecutionContextExecutor

class VisitService(
  implicit
  actorSystem: ActorSystem,
  dispatcher:  ExecutionContextExecutor
) {

  def create(visit: Visit): ServiceResult[Unit] = {
    val existsResult = VisitRepository.exists(visit.id)
    val userExistsResult = UserRepository.exists(visit.user)
    val locationExistsResult = LocationRepository.exists(visit.location)

    val checkResult = for {
      exists ← existsResult
      userExists ← userExistsResult
      locationExists ← locationExistsResult
    } yield !exists && userExists && locationExists

    checkResult.flatMap {
      case true  ⇒ VisitRepository.save(visit).mapToUnit
      case false ⇒ async(error(400))
    }
  }

  def update(id: Long, dto: VisitUpdateDto): ServiceResult[Unit] = {

    def saveUpdates() = {
      val updatedVisit = Visit(
        id        = id,
        location  = dto.location,
        user      = dto.user,
        visitedAt = dto.visitedAt,
        mark      = dto.mark
      )

      VisitRepository.update(id, updatedVisit).mapToUnit
    }

    val existsResult = VisitRepository.exists(id)
    val userExistsResult = UserRepository.exists(dto.user)
    val locationExistsResult = LocationRepository.exists(dto.location)

    val checkResult = for {
      exists ← existsResult
      userExists ← userExistsResult
      locationExists ← locationExistsResult
    } yield (exists, userExists && locationExists)

    checkResult.flatMap {
      case (true, true)  ⇒ saveUpdates()
      case (false, _)    ⇒ async(error(404))
      case (true, false) ⇒ async(error(400))
    }
  }

  def findOne(id: Long): ServiceResult[Visit] = {
    VisitRepository.findOne(id).map {
      case Some(v) ⇒ success(v)
      case None    ⇒ error(404)
    }
  }

  def find(userId: Long, params: VisitsRequestParams): ServiceResult[Seq[UserVisit]] = {
    UserRepository.exists(userId).flatMap {
      case true ⇒
        val findResult =
          LocationRepository.findIdsByGeo(params.country, params.toDistance).flatMap {
            case Nil ⇒ async(Seq.empty[UserVisit])

            case locationsIds ⇒
              VisitRepository
                .findByUserAndLocationsAndDate(userId, locationsIds, params.fromDate, params.toDate)
                .map(visits ⇒ visits.map(d ⇒ UserVisit(d._1, d._2.getTime, d._3)))
          }

        findResult.toResult

      case false ⇒ async(error(404))
    }
  }

}
