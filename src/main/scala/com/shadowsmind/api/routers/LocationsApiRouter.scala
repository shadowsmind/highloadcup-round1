package com.shadowsmind.api.routers

import java.sql.Timestamp

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.shadowsmind.api.protocol.{ ApiJsonProtocol, LocationMarksAvg }
import com.shadowsmind.models.UserGender.UserGender
import com.shadowsmind.models.{ Location, LocationUpdateDto }
import com.shadowsmind.services.LocationService

class LocationsApiRouter(locationService: LocationService) {

  import ApiJsonProtocol._

  // format: OFF
  def route: Route = pathPrefix("locations") {
    pathPrefix(LongNumber) { id ⇒
      pathEndOrSingleSlash {
        get {
          onSuccess(locationService.findOne(id)) {
            case Right(value) ⇒ complete(value)
            case Left(error)  ⇒ complete(StatusCode.int2StatusCode(error))
          }
        } ~
        post {
          entity(as[LocationUpdateDto]) { dto ⇒
            onSuccess(locationService.update(id, dto)) {
              case Right(_)    ⇒ complete(ApiJsonProtocol.EmptyBody)
              case Left(error) ⇒ complete(StatusCode.int2StatusCode(error))
            }
          }
        }
      } ~
      path("avg") {
        get {
          parameters('fromDate.as[Timestamp].?, 'toDate.as[Timestamp].?, 'fromAge.as[Int].?, 'toAge.as[Int].?, 'gender.as[UserGender].?) {
            (fromDate, toDate, fromAge, toAge, gender) =>
              onSuccess(locationService.getMarksAvg(id, fromDate, toDate, fromAge, toAge, gender)) {
                case Right(value) ⇒ complete(LocationMarksAvg(value))
                case Left(error)  ⇒ complete(StatusCode.int2StatusCode(error))
              }
          }
        }
      }
    } ~
    path("new") {
      post {
        entity(as[Location]) { location ⇒
          onSuccess(locationService.create(location)) {
            case Right(_)    ⇒ complete(ApiJsonProtocol.EmptyBody)
            case Left(error) ⇒ complete(StatusCode.int2StatusCode(error))
          }
        }
      }
    }
  }
  // format: ON

}
