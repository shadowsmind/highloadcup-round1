package com.shadowsmind

import java.sql.Timestamp

package object models {

  object UserGender extends Enumeration {
    type UserGender = Value
    val m, f = Value
  }

  import UserGender.UserGender

  trait UserFields {
    val email: String
    val firstName: String
    val lastName: String
    val gender: UserGender
    val birthDate: Timestamp
  }

  case class User(
    id:        Long, // уникальный внешний id. Устанавливается тестирующей системой. 32-разрядное целое беззнаковоее число.
    email:     String, // адрес электронной почты пользователя. Тип - unicode-строка длиной до 100 символов. Уникальное поле.
    firstName: String, // имя. Тип - unicode-строки длиной до 50 символов.
    lastName:  String, // фамилия соответственно. Тип - unicode-строки длиной до 50 символов.
    gender:    UserGender, // unicode-строка m означает мужской пол, а f - женский.
    birthDate: Timestamp // дата рождения, записанная как число секунд от начала UNIX-эпохи по UTC (другими словами - это timestamp).
  ) extends UserFields {
    def update(dto: UserUpdateDto): User = this.copy(
      email     = dto.email,
      firstName = dto.firstName,
      lastName  = dto.lastName,
      gender    = dto.gender,
      birthDate = dto.birthDate
    )
  }

  case class UserUpdateDto(
    email:     String,
    firstName: String,
    lastName:  String,
    gender:    UserGender,
    birthDate: Timestamp
  ) extends UserFields

  trait LocationFields {
    val place: String
    val country: String
    val city: String
    val distance: Long
  }

  case class Location(
    id:       Long, // уникальный внешний id. Устанавливается тестирующей системой. 32-разрядное целое беззнаковоее число.
    place:    String, // описание достопримечательности. Текстовое поле неограниченной длины.
    country:  String, // название страны расположения. unicode-строка длиной до 50 символов.
    city:     String, // название города расположения. unicode-строка длиной до 50 символов.
    distance: Long // расстояние от города по прямой в километрах. 32-разрядное целое беззнаковое число.
  ) extends LocationFields

  case class LocationUpdateDto(
    place:    String,
    country:  String,
    city:     String,
    distance: Long
  ) extends LocationFields

  trait VisitFields {
    val location: Long
    val user: Long
    val visitedAt: Timestamp
    val mark: Int
  }

  case class Visit(
    id:        Long, // уникальный внешний id. Устанавливается тестирующей системой. 32-разрядное целое беззнаковоее число.
    location:  Long, // id достопримечательности. 32-разрядное целое беззнаковое число.
    user:      Long, // id путешественника. 32-разрядное целое беззнаковое число.
    visitedAt: Timestamp, // дата посещения, timestamp.
    mark:      Int // оценка посещения от 0 до 5 включительно. Целое число.
  ) extends VisitFields

  case class VisitUpdateDto(
    location:  Long,
    user:      Long,
    visitedAt: Timestamp,
    mark:      Int
  ) extends VisitFields

}
