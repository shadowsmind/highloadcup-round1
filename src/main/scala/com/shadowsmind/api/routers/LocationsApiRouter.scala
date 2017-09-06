package com.shadowsmind.api.routers

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.shadowsmind.api.directives.CommonDirectives
import com.shadowsmind.api.protocol.{ ApiJsonProtocol, LocationMarksAvg }
import com.shadowsmind.api.validation.LocationValidator
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
        CommonDirectives.postDto(as[LocationUpdateDto], LocationValidator.validate) { dto ⇒
          onSuccess(locationService.update(id, dto)) {
            case Right(_)    ⇒ complete(ApiJsonProtocol.EmptyBody)
            case Left(error) ⇒ complete(StatusCode.int2StatusCode(error))
          }
        }
      } ~
      path("avg") {
        get {
          CommonDirectives.locationAvgParams() { params ⇒
            onSuccess(locationService.getMarksAvg(id, params)) {
              case Right(value) ⇒ complete(LocationMarksAvg(value))
              case Left(error)  ⇒ complete(StatusCode.int2StatusCode(error))
            }
          }
        }
      }
    } ~
    path("new") {
      CommonDirectives.postDto(as[Location], LocationValidator.validate) { location ⇒
        onSuccess(locationService.create(location)) {
          case Right(_)    ⇒ complete(ApiJsonProtocol.EmptyBody)
          case Left(error) ⇒ complete(StatusCode.int2StatusCode(error))
        }
      }
    }
  }
  // format: ON

}
