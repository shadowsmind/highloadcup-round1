package com.shadowsmind.api.directives

import java.sql.Timestamp

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import com.shadowsmind.api.protocol.ApiJsonProtocol
import com.shadowsmind.models.UserGender

import scala.util.{ Failure, Success, Try }

object CommonDirectives {

  import ApiJsonProtocol._

  def validDto[T](um: FromRequestUnmarshaller[T], validate: T ⇒ Boolean): Directive1[T] = {
    (post & entity(um)).flatMap { dto ⇒
      if (validate(dto)) {
        provide(dto)
      } else {
        reject(ValidationRejection("invalid data"))
      }
    }
  }

  def locationAvgParams(): Directive1[LocationAvgRequestParams] = {
    parameters('fromDate.as[Timestamp].?, 'toDate.as[Timestamp].?, 'fromAge.as[Int].?, 'toAge.as[Int].?, 'gender.as[String].?)
      .tflatMap {
        case (fromDate, toDate, fromAge, toAge, gender) ⇒
          Try(gender.map(UserGender.withName)) match {
            case Success(g) ⇒
              provide(LocationAvgRequestParams(fromDate, toDate, fromAge, toAge, g))

            case Failure(e) ⇒
              reject(MalformedQueryParamRejection("gender", e.getMessage))
          }
      }
  }

  def visitParams(): Directive1[VisitsRequestParams] = {
    parameters('fromDate.as[Timestamp].?, 'toDate.as[Timestamp].?, 'country.?, 'toDistance.as[Long].?)
      .tflatMap {
        case (fromDate, toDate, country, toDistance) ⇒
          provide(VisitsRequestParams(fromDate, toDate, country, toDistance))
      }
  }

}
