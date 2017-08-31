package com.shadowsmind

import akka.http.scaladsl.util.FastFuture

import scala.concurrent.{ ExecutionContextExecutor, Future }

package object services {

  type ServiceError = Int
  type ServiceResult[T] = Future[Either[ServiceError, T]]

  def error(statusCode: Int) = Left(statusCode)
  def success[T](value: T) = Right(value)

  def async[T](value: T) = FastFuture.successful(value)

  implicit class RichFuture[T](future: Future[T]) {

    def mapToUnit(implicit ex: ExecutionContextExecutor): ServiceResult[Unit] =
      future.map(_ ⇒ success(()))

    def mapToResult[R](f: T ⇒ R)(implicit ex: ExecutionContextExecutor): ServiceResult[R] =
      future.map(v ⇒ success(f(v)))

    def toResult(implicit ex: ExecutionContextExecutor): ServiceResult[T] =
      future.map(success)

  }

  implicit class RichFutureOption[T](future: Future[Option[T]]) {

    def resultOrNotFound(implicit ex: ExecutionContextExecutor): ServiceResult[T] =
      future.map {
        case Some(v) ⇒ success(v)
        case None    ⇒ error(404)
      }

  }

}
