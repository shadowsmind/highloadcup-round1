package com.shadowsmind

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ HttpApp, MethodRejection, RejectionHandler, Route }

class WebServer(route: Route) extends HttpApp {

  val rejectionsHandler: RejectionHandler = RejectionHandler.newBuilder()
    .handleAll[MethodRejection] { _ ⇒
      complete(StatusCodes.NotFound)
    }
    .result()

  override protected def routes: Route = handleRejections(rejectionsHandler) {
    route
  }

}

object WebServer {

  def apply(route: Route): WebServer = new WebServer(route)

}