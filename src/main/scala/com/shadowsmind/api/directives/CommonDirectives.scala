package com.shadowsmind.api.directives

import java.sql.Timestamp

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import com.shadowsmind.api.protocol.ApiJsonProtocol
import com.shadowsmind.models.UserGender.UserGender

object CommonDirectives {

  import ApiJsonProtocol._

  // TODO: add validation with function as param
  def postDto[T](um: FromRequestUnmarshaller[T]): Directive1[T] = post & entity(um)

  def locationAvgParams(): Directive1[LocationAvgRequestParams] = {
    parameters('fromDate.as[Timestamp].?, 'toDate.as[Timestamp].?, 'fromAge.as[Int].?, 'toAge.as[Int].?, 'gender.as[UserGender].?)
      .tflatMap {
        case (fromDate, toDate, fromAge, toAge, gender) ⇒
          provide(LocationAvgRequestParams(fromDate, toDate, fromAge, toAge, gender))
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
