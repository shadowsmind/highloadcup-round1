package com.shadowsmind

package object config {

  case class AppConfig(
    server:   ServerConfig,
    database: DatabaseConfig,
    storage:  Storage
  )

  case class ServerConfig(
    host: String,
    port: Int
  )

  case class DatabaseConfig(
    driver:         String,
    jdbcUrl:        String,
    username:       String,
    password:       String,
    maxConnections: Int
  )

  case class Storage(
    archivePath: String
  )

}
