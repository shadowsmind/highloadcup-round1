package com.shadowsmind.api.routers

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.shadowsmind.api.directives.CommonDirectives
import com.shadowsmind.models.{ Visit, VisitUpdateDto }
import com.shadowsmind.api.protocol.ApiJsonProtocol
import com.shadowsmind.api.validation.{ VisitCreateValidator, VisitUpdateValidator }
import com.shadowsmind.services.VisitService

class VisitsApiRouter(visitService: VisitService) {

  import ApiJsonProtocol._

  // format: OFF
  def route: Route = pathPrefix("visits") {
    pathPrefix(LongNumber) { id ⇒
      pathEndOrSingleSlash {
        get {
          onSuccess(visitService.findOne(id)) {
            case Right(value) ⇒ complete(value)
            case Left(error)  ⇒ complete(StatusCode.int2StatusCode(error))
          }
        } ~
        CommonDirectives.validDto(as[VisitUpdateDto], VisitUpdateValidator.validate) { dto ⇒
          onSuccess(visitService.update(id, dto)) {
            case Right(_)    ⇒ complete(ApiJsonProtocol.EmptyBody)
            case Left(error) ⇒ complete(StatusCode.int2StatusCode(error))
          }
        }
      }
    } ~
    path("new") {
      CommonDirectives.validDto(as[Visit], VisitCreateValidator.validate) { visit ⇒
        onSuccess(visitService.create(visit)) {
          case Right(_)    ⇒ complete(ApiJsonProtocol.EmptyBody)
          case Left(error) ⇒ complete(StatusCode.int2StatusCode(error))
        }
      }
    }
  }
  // format: ON

}
