package com.shadowsmind.services

import akka.actor.ActorSystem
import com.shadowsmind.api.directives.LocationAvgRequestParams
import com.shadowsmind.models.{ Location, LocationUpdateDto, Visit }
import com.shadowsmind.persistence.{ LocationRepository, UserRepository, VisitRepository }
import com.shadowsmind.utils.DateHelper

import scala.concurrent.ExecutionContextExecutor

class LocationService(
  implicit
  actorSystem: ActorSystem,
  dispatcher:  ExecutionContextExecutor
) {

  def create(location: Location): ServiceResult[Unit] = {
    LocationRepository.exists(location.id).flatMap {
      case true  ⇒ async(error(400))
      case false ⇒ LocationRepository.save(location).mapToUnit
    }
  }

  def update(id: Long, dto: LocationUpdateDto): ServiceResult[Unit] = {

    def updateAndSave(location: Location) = {
      val updatedLocation = location.copy(
        place    = dto.place.getOrElse(location.place),
        country  = dto.country.getOrElse(location.country),
        city     = dto.city.getOrElse(location.city),
        distance = dto.distance.getOrElse(location.distance)
      )

      LocationRepository.update(id, updatedLocation).mapToUnit
    }

    LocationRepository.findOne(id).flatMap {
      case Some(location) ⇒ updateAndSave(location)
      case None           ⇒ async(error(404))
    }
  }

  def findOne(id: Long): ServiceResult[Location] = {
    LocationRepository.findOne(id).map {
      case Some(v) ⇒ success(v)
      case None    ⇒ error(404)
    }
  }

  def getMarksAvg(id: Long, params: LocationAvgRequestParams): ServiceResult[Double] = {

    def calculateMarksAvg(visits: Seq[Visit]): Double = {
      import scala.math.BigDecimal.RoundingMode.HALF_UP

      if (visits.isEmpty) {
        0.0
      } else {
        val markSum = visits.map(_.mark).sum.toDouble
        BigDecimal(markSum / visits.size).setScale(5, HALF_UP).toDouble
      }
    }

    LocationRepository.exists(id).flatMap {
      case true ⇒
        val avgResult =
          if (params.fromDate.isEmpty && params.toDate.isEmpty && params.fromAge.isEmpty && params.toAge.isEmpty && params.gender.isEmpty) {
            VisitRepository.findByLocation(id)
              .map(calculateMarksAvg)
          } else if (params.fromAge.isEmpty && params.toAge.isEmpty && params.gender.isEmpty) {
            VisitRepository.findByLocationAndDate(id, params.fromDate, params.toDate)
              .map(calculateMarksAvg)
          } else {
            val fromBirthday = params.fromAge.map(DateHelper.yearsAgo)
            val toBirthday = params.toAge.map(DateHelper.yearsAgo)
            UserRepository.findIdsByBirthdayAndGender(fromBirthday, toBirthday, params.gender)
              .flatMap {
                case Nil ⇒ async(0.0)

                case usersIds ⇒
                  VisitRepository.findByLocationAndUsersAndDate(id, usersIds, params.fromDate, params.toDate)
                    .map(calculateMarksAvg)
              }
          }

        avgResult.toResult

      case false ⇒ async(error(404))
    }
  }

}