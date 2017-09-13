package com.shadowsmind.api.routers

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.shadowsmind.api.directives.CommonDirectives
import com.shadowsmind.api.protocol.ApiJsonProtocol
import com.shadowsmind.api.validation.UserValidator
import com.shadowsmind.models.{ User, UserUpdateDto, UserVisitsDto }
import com.shadowsmind.services.{ UserService, VisitService }

class UsersApiRouter(userService: UserService, visitService: VisitService) {

  import ApiJsonProtocol._

  // format: OFF
  def route: Route = pathPrefix("users") {
    pathPrefix(LongNumber) { id ⇒
      pathEndOrSingleSlash {
        get {
          onSuccess(userService.findOne(id)) {
            case Right(value) ⇒ complete(value)
            case Left(error)  ⇒ complete(StatusCode.int2StatusCode(error))
          }
        } ~
        CommonDirectives.validDto(as[UserUpdateDto], UserValidator.validate) { dto ⇒
          onSuccess(userService.update(id, dto)) {
            case Right(_)    ⇒ complete(ApiJsonProtocol.EmptyBody)
            case Left(error) ⇒ complete(StatusCode.int2StatusCode(error))
          }
        }
      } ~
      path("visits") {
        get {
          CommonDirectives.visitParams() { params ⇒
            onSuccess(visitService.find(id, params)) {
              case Right(value) ⇒ complete(UserVisitsDto(value))
              case Left(error)  ⇒ complete(StatusCode.int2StatusCode(error))
            }
          }
        }
      }
    } ~
    path("new") {
      CommonDirectives.validDto(as[User], UserValidator.validate) { user ⇒
        onSuccess(userService.create(user)) {
          case Right(_)    ⇒ complete(ApiJsonProtocol.EmptyBody)
          case Left(error) ⇒ complete(StatusCode.int2StatusCode(error))
        }
      }
    }
  }
  // format: ON

}
