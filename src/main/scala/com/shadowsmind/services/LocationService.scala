package com.shadowsmind.services

import java.sql.Timestamp

import akka.actor.ActorSystem
import com.shadowsmind.api.directives.LocationAvgRequestParams
import com.shadowsmind.models.UserGender.UserGender
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
    LocationRepository.findOne(location.id).flatMap {
      case Some(_) ⇒ async(error(400))
      case None    ⇒ LocationRepository.save(location).mapToUnit
    }
  }

  def update(id: Long, dto: LocationUpdateDto): ServiceResult[Unit] = {
    LocationRepository.findOne(id).flatMap {
      case Some(location) ⇒
        val updatedLocation = location.update(dto)
        LocationRepository.update(id, updatedLocation).mapToUnit

      case None ⇒ async(error(404))
    }
  }

  def findOne(id: Long): ServiceResult[Location] = {
    LocationRepository.findOne(id).map {
      case Some(v) ⇒ success(v)
      case None    ⇒ error(404)
    }
  }

  def getMarksAvg(id: Long, params: LocationAvgRequestParams): ServiceResult[Double] = {

    def calculateMarksAvg(visits: Seq[Visit]): Option[Double] = {
      if (visits.isEmpty) {
        Some(0.0)
      } else {
        val markSum = visits.map(_.mark).sum.toDouble
        Some(markSum / visits.size)
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
                case Nil ⇒ async(Some(0.0))

                case usersIds ⇒
                  VisitRepository.findByLocationAndUsersAndDate(id, usersIds, params.fromDate, params.toDate)
                    .map(calculateMarksAvg)
              }
          }

        avgResult.resultOrNotFound

      case false ⇒ async(error(404))
    }
  }

}