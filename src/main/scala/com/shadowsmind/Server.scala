package com.shadowsmind

import akka.actor.ActorSystem
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.ActorMaterializer
import com.shadowsmind.api.routers.ApiRouter
import com.softwaremill.macwire.wire
import com.shadowsmind.config.ConfigKeeper
import com.shadowsmind.loader.DataLoader
import com.shadowsmind.persistence.DatabaseSchemeMigration
import com.shadowsmind.services.{ LocationService, UserService, VisitService }

object Server extends App {

  implicit val stopSystem: () ⇒ Unit = () ⇒ sys.exit()

  val config = ConfigKeeper.appConfig

  implicit val actorSystem = ActorSystem("HighLoadCupSystem")
  implicit val dispatcher = actorSystem.dispatcher
  implicit val materializer = ActorMaterializer()

  DatabaseSchemeMigration.migrate

  val systemActor = actorSystem.actorOf(SystemActor.props)

  val userService = wire[UserService]
  val locationService = wire[LocationService]
  val visitService = wire[VisitService]

  val apiRouter = wire[ApiRouter]

  DataLoader.load(config.storage.dataPath, config.storage.isZip)

  WebServer(apiRouter.route())
    .startServer(config.server.host, config.server.port, ServerSettings(actorSystem), actorSystem)

}