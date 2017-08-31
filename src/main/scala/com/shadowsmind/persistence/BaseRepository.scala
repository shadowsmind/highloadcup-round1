package com.shadowsmind.persistence

import slick.dbio.Effect.Write
import slick.sql.FixedSqlAction
import scala.concurrent.Future
import DatabaseConnection.api._
import DatabaseConnection.DB

class BaseRepository[E, T <: BaseTable[E]](val entities: TableQuery[T]) {

  val tableName: String = entities.baseTableRow.tableName

  private[persistence] def createSchema: Future[Unit] =
    DB.run(entities.schema.create)

  // actions
  def existsAction(id: Long): Rep[Boolean] =
    entities.filter(_.id === id).exists

  def saveAction(entity: E): FixedSqlAction[E, NoStream, Write] =
    entities returning entities += entity

  def saveAllAction(entitiesList: Seq[E]): FixedSqlAction[Seq[E], NoStream, Write] =
    entities returning entities ++= entitiesList

  def updateAction(id: Long, entity: E): FixedSqlAction[Int, NoStream, Write] =
    idFilter(id).update(entity)

  // filters
  def idFilter(id: Long): Query[T, E, Seq] =
    entities.filter(_.id === id)

  def idsFilter(ids: Seq[Long]): Query[T, E, Seq] =
    entities.filter(_.id inSet ids)

  // queries
  def exists(id: Long): Future[Boolean] =
    DB.run(existsAction(id).result)

  def save(entity: E): Future[E] =
    DB.run(saveAction(entity).transactionally)

  def saveAll(entities: Seq[E]): Future[Seq[E]] =
    DB.run(saveAllAction(entities).transactionally)

  def update(id: Long, entity: E): Future[Int] =
    DB.run(updateAction(id, entity).transactionally)

  def findOne(id: Long): Future[Option[E]] =
    DB.run(idFilter(id).result.headOption)

  def findByIds(ids: Seq[Long]): Future[Seq[E]] =
    DB.run(idsFilter(ids).result)

  def findAll: Future[Seq[E]] =
    DB.run(entities.result)

}
