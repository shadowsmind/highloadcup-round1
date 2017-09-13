package com.shadowsmind.services

import akka.actor.ActorSystem
import com.shadowsmind.api.directives.VisitsRequestParams
import com.shadowsmind.models.{ UserVisit, Visit, VisitUpdateDto }
import com.shadowsmind.persistence.{ LocationRepository, UserRepository, VisitRepository }
import com.shadowsmind.utils.FutureHelper.RichAsyncOption

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

    def updateAndSave(visit: Visit) = {
      val updatedVisit = visit.copy(
        location  = dto.location.getOrElse(visit.location),
        user      = dto.user.getOrElse(visit.user),
        visitedAt = dto.visitedAt.getOrElse(visit.visitedAt),
        mark      = dto.mark.getOrElse(visit.mark)
      )

      VisitRepository.update(id, updatedVisit).mapToUnit
    }

    def checkAndUpdate(visit: Visit) = {
      val userExistsResult = dto.user.foldAsync(true)(UserRepository.exists)
      val locationExistsResult = dto.location.foldAsync(true)(LocationRepository.exists)

      val checkResult = for {
        userExists ← userExistsResult
        locationExists ← locationExistsResult
      } yield userExists && locationExists

      checkResult.flatMap {
        case true  ⇒ updateAndSave(visit)
        case false ⇒ async(error(400))
      }
    }

    VisitRepository.findOne(id).flatMap {
      case Some(visit) ⇒ checkAndUpdate(visit)
      case None        ⇒ async(error(404))
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
