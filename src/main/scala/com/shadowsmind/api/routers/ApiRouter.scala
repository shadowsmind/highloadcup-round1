package com.shadowsmind.api.routers

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.shadowsmind.services.{ LocationService, UserService, VisitService }
import com.softwaremill.macwire.wire

class ApiRouter(
  userService:     UserService,
  locationService: LocationService,
  visitService:    VisitService
) {

  val usersApiRouter = wire[UsersApiRouter]
  val locationsApiRouter = wire[LocationsApiRouter]
  val visitsApiRouter = wire[VisitsApiRouter]

  // format: OFF
  def route(): Route = {
    usersApiRouter.route ~
    locationsApiRouter.route ~
    visitsApiRouter.route
  }
  // format: ON

}