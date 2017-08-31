package com.shadowsmind.api

import com.shadowsmind.models._

package object protocol {

  case class Users(
    users: Seq[User]
  )

  case class Locations(
    locations: Seq[Location]
  )

  case class LocationMarksAvg(
    avg: Double
  )

  case class Visits(
    visits: Seq[Visit]
  )

}
