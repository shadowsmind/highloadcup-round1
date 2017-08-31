package com.shadowsmind.services

import akka.actor.ActorSystem
import com.shadowsmind.models.{ User, UserUpdateDto }
import com.shadowsmind.persistence.UserRepository

import scala.concurrent.ExecutionContextExecutor

class UserService(
  implicit
  actorSystem: ActorSystem,
  dispatcher:  ExecutionContextExecutor
) {

  def create(user: User): ServiceResult[Unit] = {
    UserRepository.findOne(user.id).flatMap {
      case Some(_) ⇒ async(error(400))
      case None    ⇒ UserRepository.save(user).mapToUnit
    }
  }

  def update(id: Long, dto: UserUpdateDto): ServiceResult[Unit] = {
    UserRepository.findOne(id).flatMap {
      case Some(user) ⇒
        val updatedUser = user.update(dto)
        UserRepository.update(id, updatedUser).mapToUnit

      case None ⇒ async(error(404))
    }
  }

  def findOne(id: Long): ServiceResult[User] = {
    UserRepository.findOne(id).map {
      case Some(v) ⇒ success(v)
      case None    ⇒ error(404)
    }
  }

}
