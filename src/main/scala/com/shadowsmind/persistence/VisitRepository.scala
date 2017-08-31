package com.shadowsmind.persistence

import java.sql.Timestamp

import com.shadowsmind.models.Visit
import DatabaseConnection.api._
import DatabaseConnection.{ DB, OptionalFilter }
import slick.lifted.ProvenShape

import scala.concurrent.Future

object VisitRepository extends BaseRepository[Visit, VisitTable](TableQuery[VisitTable]) {

  // filters
  def byUserFilter(userId: Long): Query[VisitTable, Visit, Seq] =
    entities.filter(_.user === userId)

  def byLocationFilter(locationId: Long): Query[VisitTable, Visit, Seq] =
    entities.filter(_.location === locationId)

  def byDateFilter(
    otherFilter: Query[VisitTable, Visit, Seq],
    fromDate:    Option[Timestamp], toDate: Option[Timestamp]
  ): Query[VisitTable, Visit, Seq] =
    OptionalFilter(otherFilter)
      .filter(fromDate)(date ⇒ v ⇒ v.visitedAt > date)
      .filter(toDate)(date ⇒ v ⇒ v.visitedAt < date)
      .query

  def joinLocationsFilter(
    otherFilter: Query[VisitTable, Visit, Seq],
    country:     Option[String], toDistance: Option[Long]
  ): Query[VisitTable, Visit, Seq] =
    OptionalFilter(otherFilter.join(LocationRepository.entities).on(_.location === _.id))
      .filter(country)(c ⇒ v ⇒ v._2.country === c)
      .filter(toDistance)(d ⇒ v ⇒ v._2.distance < d)
      .query
      .map(_._1)

  // queries
  def findByUser(userId: Long): Future[Seq[Visit]] =
    DB.run {
      byUserFilter(userId).result
    }

  def findByUserAndDate(userId: Long, fromDate: Option[Timestamp], toDate: Option[Timestamp]) =
    DB.run {
      byDateFilter(byUserFilter(userId), fromDate, toDate).result
    }

  def findByUserAndDateAndLocations(
    userId:   Long,
    fromDate: Option[Timestamp], toDate: Option[Timestamp],
    country: Option[String], toDistance: Option[Long]
  ): Future[Seq[Visit]] = {
    val baseFilter = if (fromDate.isDefined || toDate.isDefined) {
      byDateFilter(byUserFilter(userId), fromDate, toDate)
    } else {
      byUserFilter(userId)
    }

    val filtered = if (country.isDefined || toDistance.isDefined) {
      joinLocationsFilter(baseFilter, country, toDistance)
    } else {
      baseFilter
    }

    DB.run(filtered.sortBy(_.visitedAt).result)
  }

  def findByUserAndLocationsAndDate(
    userId:       Long,
    locationsIds: Seq[Long],
    fromDate:     Option[Timestamp], toDate: Option[Timestamp]
  ): Future[Seq[Visit]] = {
    val byUserAndLocations = byUserFilter(userId).filter(_.location inSet locationsIds)

    val byDate = if (fromDate.isDefined || toDate.isDefined) {
      byDateFilter(byUserAndLocations, fromDate, toDate)
    } else {
      byUserAndLocations
    }

    DB.run(byDate.sortBy(_.visitedAt).result)
  }

  def findByLocation(locationId: Long): Future[Seq[Visit]] =
    DB.run {
      byLocationFilter(locationId).result
    }

  def findByLocationAndDate(locationId: Long, fromDate: Option[Timestamp], toDate: Option[Timestamp]): Future[Seq[Visit]] =
    DB.run {
      byDateFilter(byLocationFilter(locationId), fromDate, toDate).result
    }

  def findByLocationAndUsersAndDate(
    locationId: Long,
    usersIds:   Seq[Long],
    fromDate:   Option[Timestamp], toDate: Option[Timestamp]
  ): Future[Seq[Visit]] = {
    val byLocationAndUsers = byLocationFilter(locationId).filter(_.user inSet usersIds)

    val filtered = if (fromDate.isDefined || toDate.isDefined) {
      byDateFilter(byLocationAndUsers, fromDate, toDate)
    } else {
      byLocationAndUsers
    }

    DB.run(filtered.result)
  }

}

class VisitTable(tag: Tag) extends BaseTable[Visit](tag, "visits") {

  // format: OFF
  def location  = column[Long]("location_id")
  def user      = column[Long]("user_id")
  def visitedAt = column[Timestamp]("visited_at")
  def mark      = column[Int]("mark")
  // format: ON

  override def * : ProvenShape[Visit] =
    (id, location, user, visitedAt, mark) <> (Visit.tupled, Visit.unapply)

}