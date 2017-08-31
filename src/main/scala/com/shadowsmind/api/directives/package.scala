package com.shadowsmind.api

import java.sql.Timestamp

import com.shadowsmind.models.UserGender.UserGender

package object directives {

  case class LocationAvgRequestParams(
    fromDate: Option[Timestamp],
    toDate:   Option[Timestamp],
    fromAge:  Option[Int],
    toAge:    Option[Int],
    gender:   Option[UserGender]
  )

  case class VisitsRequestParams(
    fromDate:   Option[Timestamp],
    toDate:     Option[Timestamp],
    country:    Option[String],
    toDistance: Option[Long]
  )

}
