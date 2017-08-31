package com.shadowsmind.persistence

import com.github.tminglei.slickpg._
import com.shadowsmind.config.ConfigKeeper
import com.shadowsmind.models.UserGender
import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }

trait DatabaseConnection extends ExPostgresProfile
  with PgEnumSupport
  with PgDateSupport {

  private val config = ConfigKeeper.appConfig.database

  private val hikariConfig = new HikariConfig()

  hikariConfig.setJdbcUrl(config.jdbcUrl)
  hikariConfig.setUsername(config.username)
  hikariConfig.setPassword(config.password)
  hikariConfig.setDriverClassName(config.driver)

  val dataSource = new HikariDataSource(hikariConfig)

  override val api: API = new API {}

  val DB: backend.DatabaseDef = api.Database.forDataSource(dataSource, Some(config.maxConnections))

  trait API extends super.API with EnumApi {}

  trait EnumApi { this: API ⇒
    implicit val userGenderMapper = createEnumJdbcType("user_gender", UserGender)
  }

}

object DatabaseConnection extends DatabaseConnection {

  import slick.lifted.CanBeQueryCondition
  // optionally filter on a column with a supplied predicate
  case class OptionalFilter[X, Y, C[_]](query: slick.lifted.Query[X, Y, C]) {
    def filter[T, R: CanBeQueryCondition](data: Option[T])(f: T ⇒ X ⇒ R): OptionalFilter[X, Y, C] = {
      data.map(v ⇒ OptionalFilter(query.withFilter(f(v)))).getOrElse(this)
    }
  }

}

