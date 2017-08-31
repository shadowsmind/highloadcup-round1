package com.shadowsmind.persistence

import com.shadowsmind.models.Location
import DatabaseConnection.api._
import DatabaseConnection.{ DB, OptionalFilter }
import slick.lifted.ProvenShape

import scala.concurrent.Future

object LocationRepository extends BaseRepository[Location, LocationTable](TableQuery[LocationTable]) {

  def byGeoFilter(country: Option[String], toDistance: Option[Long]) =
    OptionalFilter(entities)
      .filter(country)(c ⇒ l ⇒ l.country === c)
      .filter(toDistance)(d ⇒ l ⇒ l.distance < d)
      .query

  def findByGeo(country: Option[String], toDistance: Option[Long]): Future[Seq[Location]] =
    DB.run(byGeoFilter(country, toDistance).result)

  def findIdsByGeo(country: Option[String], toDistance: Option[Long]): Future[Seq[Long]] =
    DB.run(byGeoFilter(country, toDistance).map(_.id).result)

}

class LocationTable(tag: Tag) extends BaseTable[Location](tag, "locations") {

  // format: OFF
  def place      = column[String]("place")
  def country    = column[String]("country")
  def city       = column[String]("city")
  def distance   = column[Long]("distance")
  // format: ON

  override def * : ProvenShape[Location] =
    (id, place, country, city, distance) <> (Location.tupled, Location.unapply)

}