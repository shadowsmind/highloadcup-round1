package com.shadowsmind.persistence

import DatabaseConnection.api._

abstract class BaseTable[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {

  // primary key
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey)

}
