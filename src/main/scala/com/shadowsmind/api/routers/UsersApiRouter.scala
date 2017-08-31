package com.shadowsmind.api.routers

import java.sql.Timestamp

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.shadowsmind.api.protocol.{ ApiJsonProtocol, Visits }
import com.shadowsmind.models.{ User, UserUpdateDto }
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
        post {
          entity(as[UserUpdateDto]) { dto ⇒
            onSuccess(userService.update(id, dto)) {
              case Right(_)    ⇒ complete(ApiJsonProtocol.EmptyBody)
              case Left(error) ⇒ complete(StatusCode.int2StatusCode(error))
            }
          }
        }
      } ~
      path("visits") {
        get {
          parameters('fromDate.as[Timestamp].?, 'toDate.as[Timestamp].?, 'country.?, 'toDistance.as[Long].?) {
            (fromDate, toDate, country, toDistance) ⇒
              onSuccess(visitService.find(id, fromDate, toDate, country, toDistance)) {
                case Right(value) ⇒ complete(Visits(value))
                case Left(error)  ⇒ complete(StatusCode.int2StatusCode(error))
              }
          }
        }
      }
    } ~
    path("new") {
      post {
        entity(as[User]) { user ⇒
          onSuccess(userService.create(user)) {
            case Right(_)    ⇒ complete(ApiJsonProtocol.EmptyBody)
            case Left(error) ⇒ complete(StatusCode.int2StatusCode(error))
          }
        }
      }
    }
  }
  // format: ON

}
