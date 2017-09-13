package com.shadowsmind.utils

import akka.http.scaladsl.util.FastFuture

import scala.concurrent.Future

object FutureHelper {

  implicit class RichAsyncOption[T](option: Option[T]) {

    def mapAsync[R](f: T ⇒ Future[Option[R]]): Future[Option[R]] = {
      option.fold(FastFuture.successful[Option[R]](None))(f)
    }

    def foldAsync[R](orElse: R)(f: T ⇒ Future[R]): Future[R] = {
      option match {
        case Some(value) ⇒ f(value)
        case None        ⇒ FastFuture.successful(orElse)
      }
    }

  }

}
