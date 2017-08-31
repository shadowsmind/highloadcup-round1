package com.shadowsmind.api.protocol

import java.sql.Timestamp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.shadowsmind.utils.DateHelper
import com.shadowsmind.models._
import spray.json._

trait ApiJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit object timestampFormat extends RootJsonFormat[Timestamp] {
    override def write(obj: Timestamp): JsValue = JsNumber(obj.getTime)

    override def read(json: JsValue): Timestamp = json match {
      case JsNumber(value) ⇒ DateHelper.time(value.toLong)
      case _               ⇒ throw DeserializationException("wrong timestamp value")
    }
  }

  // format: OFF
  implicit val userGenderFormat = EnumJsonFormat(UserGender)
  implicit val userFormat       = jsonFormat(User.apply, "id", "email", "first_name", "last_name", "gender", "birth_date")
  implicit val usersFormat      = jsonFormat1(Users)
  implicit val userUpdateFormat = jsonFormat(UserUpdateDto.apply, "email", "first_name", "last_name", "gender", "birth_date")

  implicit val locationFormat        = jsonFormat5(Location)
  implicit val locationsFormat       = jsonFormat1(Locations)
  implicit val locationMarkAvgFormat = jsonFormat1(LocationMarksAvg)

  implicit val locationUpdateFormat = jsonFormat4(LocationUpdateDto)

  implicit val visitFormat       = jsonFormat(Visit.apply, "id", "location", "user", "visited_at", "mark")
  implicit val visitsFormat      = jsonFormat1(Visits)
  implicit val visitUpdateFormat = jsonFormat(VisitUpdateDto.apply, "location", "user", "visited_at", "mark")
  // format: ON

}

object ApiJsonProtocol extends ApiJsonProtocol {

  val EmptyBody = JsObject.empty

}