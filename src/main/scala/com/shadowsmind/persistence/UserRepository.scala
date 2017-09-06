package com.shadowsmind.persistence

import java.sql.Timestamp

import com.shadowsmind.models.{ User, UserGender }
import DatabaseConnection.api._
import DatabaseConnection.{ DB, OptionalFilter }
import com.shadowsmind.models.UserGender.UserGender
import slick.jdbc.JdbcType
import slick.lifted.ProvenShape

import scala.concurrent.Future

object UserRepository extends BaseRepository[User, UserTable](TableQuery[UserTable]) {

  import UserTableMappers._

  def byBirthdayAndGenderFilter(from: Option[Timestamp], to: Option[Timestamp], gender: Option[UserGender]): Query[UserTable, User, Seq] =
    OptionalFilter(entities)
      .filter(from)(b ⇒ u ⇒ u.birthDate > b)
      .filter(to)(b ⇒ u ⇒ u.birthDate < b)
      .filter(gender)(g ⇒ u ⇒ u.gender === g)
      .query

  def existsByIdOrEmail(id: Long, email: String): Future[Boolean] =
    DB.run {
      entities.filter(u ⇒ (u.id === id) || (u.email === email)).exists.result
    }

  def findByIdOrEmail(id: Long, email: String): Future[Seq[User]] =
    DB.run {
      entities.filter(u ⇒ (u.id === id) || (u.email === email)).result
    }

  def findByBirthdayAndGender(from: Option[Timestamp], to: Option[Timestamp], gender: Option[UserGender]): Future[Seq[User]] =
    DB.run {
      byBirthdayAndGenderFilter(from, to, gender).result
    }

  def findIdsByBirthdayAndGender(from: Option[Timestamp], to: Option[Timestamp], gender: Option[UserGender]): Future[Seq[Long]] =
    DB.run {
      byBirthdayAndGenderFilter(from, to, gender).map(_.id).result
    }

}

class UserTable(tag: Tag) extends BaseTable[User](tag, "users") {

  import UserTableMappers._

  // format: OFF
  def email     = column[String]("email")
  def firstName = column[String]("first_name")
  def lastName  = column[String]("last_name")
  def gender    = column[UserGender]("gender")
  def birthDate = column[Timestamp]("birth_date")
  // format: ON

  override def * : ProvenShape[User] =
    (id, email, firstName, lastName, gender, birthDate) <> (User.tupled, User.unapply)

}

object UserTableMappers {

  implicit val GenderMapper: JdbcType[UserGender] = MappedColumnType.base[UserGender, String](
    e ⇒ e.toString,
    s ⇒ UserGender.withName(s)
  )

}