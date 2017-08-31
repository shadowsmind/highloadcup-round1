package com.shadowsmind

import java.sql.Timestamp

package object models {

  object UserGender extends Enumeration {
    type UserGender = Value
    val m, f = Value
  }

  case class User(
    id:        Long, // уникальный внешний id. Устанавливается тестирующей системой. 32-разрядное целое беззнаковоее число.
    email:     String, // адрес электронной почты пользователя. Тип - unicode-строка длиной до 100 символов. Уникальное поле.
    firstName: String, // имя. Тип - unicode-строки длиной до 50 символов.
    lastName:  String, // фамилия соответственно. Тип - unicode-строки длиной до 50 символов.
    gender:    UserGender.UserGender, // unicode-строка m означает мужской пол, а f - женский.
    birthDate: Timestamp // дата рождения, записанная как число секунд от начала UNIX-эпохи по UTC (другими словами - это timestamp).
  ) {
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
    gender:    UserGender.UserGender,
    birthDate: Timestamp
  )

  case class Location(
    id:       Long, // уникальный внешний id. Устанавливается тестирующей системой. 32-разрядное целое беззнаковоее число.
    place:    String, // описание достопримечательности. Текстовое поле неограниченной длины.
    country:  String, // название страны расположения. unicode-строка длиной до 50 символов.
    city:     String, // название города расположения. unicode-строка длиной до 50 символов.
    distance: Long // расстояние от города по прямой в километрах. 32-разрядное целое беззнаковое число.
  ) {
    def update(dto: LocationUpdateDto): Location = this.copy(
      place    = dto.place,
      country  = dto.country,
      city     = dto.city,
      distance = dto.distance
    )
  }

  case class LocationUpdateDto(
    place:    String,
    country:  String,
    city:     String,
    distance: Long
  )

  case class Visit(
    id:        Long, // уникальный внешний id. Устанавливается тестирующей системой. 32-разрядное целое беззнаковоее число.
    location:  Long, // id достопримечательности. 32-разрядное целое беззнаковое число.
    user:      Long, // id путешественника. 32-разрядное целое беззнаковое число.
    visitedAt: Timestamp, // дата посещения, timestamp.
    mark:      Int // оценка посещения от 0 до 5 включительно. Целое число.
  ) {
    def update(dto: VisitUpdateDto): Visit = this.copy(
      location  = dto.location,
      user      = dto.user,
      visitedAt = dto.visitedAt,
      mark      = dto.mark
    )
  }

  case class VisitUpdateDto(
    location:  Long,
    user:      Long,
    visitedAt: Timestamp,
    mark:      Int
  )

}
