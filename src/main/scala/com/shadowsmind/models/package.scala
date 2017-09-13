package com.shadowsmind

import java.sql.Timestamp

package object models {

  // User models
  object UserGender extends Enumeration {
    type UserGender = Value
    val m, f = Value
  }

  import UserGender.UserGender

  case class User(
    id:        Long, // уникальный внешний id. Устанавливается тестирующей системой. 32-разрядное целое беззнаковоее число.
    email:     String, // адрес электронной почты пользователя. Тип - unicode-строка длиной до 100 символов. Уникальное поле.
    firstName: String, // имя. Тип - unicode-строки длиной до 50 символов.
    lastName:  String, // фамилия соответственно. Тип - unicode-строки длиной до 50 символов.
    gender:    UserGender, // unicode-строка m означает мужской пол, а f - женский.
    birthDate: Timestamp // дата рождения, записанная как число секунд от начала UNIX-эпохи по UTC (другими словами - это timestamp).
  )

  case class UserUpdateDto(
    email:     Option[String],
    firstName: Option[String],
    lastName:  Option[String],
    gender:    Option[UserGender],
    birthDate: Option[Timestamp]
  )

  case class UsersDto(
    users: Seq[User]
  )

  // Location models
  case class Location(
    id:       Long, // уникальный внешний id. Устанавливается тестирующей системой. 32-разрядное целое беззнаковоее число.
    place:    String, // описание достопримечательности. Текстовое поле неограниченной длины.
    country:  String, // название страны расположения. unicode-строка длиной до 50 символов.
    city:     String, // название города расположения. unicode-строка длиной до 50 символов.
    distance: Long // расстояние от города по прямой в километрах. 32-разрядное целое беззнаковое число.
  )

  case class LocationUpdateDto(
    place:    Option[String],
    country:  Option[String],
    city:     Option[String],
    distance: Option[Long]
  )

  case class LocationsDto(
    locations: Seq[Location]
  )

  case class LocationMarksAvgDto(
    avg: Double
  )

  // Visit models
  case class Visit(
    id:        Long, // уникальный внешний id. Устанавливается тестирующей системой. 32-разрядное целое беззнаковоее число.
    location:  Long, // id достопримечательности. 32-разрядное целое беззнаковое число.
    user:      Long, // id путешественника. 32-разрядное целое беззнаковое число.
    visitedAt: Timestamp, // дата посещения, timestamp.
    mark:      Int // оценка посещения от 0 до 5 включительно. Целое число.
  )

  case class VisitUpdateDto(
    location:  Option[Long],
    user:      Option[Long],
    visitedAt: Option[Timestamp],
    mark:      Option[Int]
  )

  case class UserVisit(
    mark:      Int,
    visitedAt: Long,
    place:     String
  )

  case class VisitsDto(
    visits: Seq[Visit]
  )

  case class UserVisitsDto(
    visits: Seq[UserVisit]
  )

}
