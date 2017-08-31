package com.shadowsmind.config

import com.typesafe.config.{ Config, ConfigFactory }
import pureconfig.loadConfigOrThrow

object ConfigKeeper {

  val config: Config = ConfigFactory.load()

  val appConfig: AppConfig = loadConfigOrThrow[AppConfig](config)

}
