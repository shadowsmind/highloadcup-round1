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
    UserRepository.existsByIdOrEmail(user.id, user.email).flatMap {
      case true  ⇒ async(error(400))
      case false ⇒ UserRepository.save(user).mapToUnit
    }
  }

  def update(id: Long, dto: UserUpdateDto): ServiceResult[Unit] = {

    def updateAndSave(user: User) = {
      val updatedUser = user.copy(
        email     = dto.email.getOrElse(user.email),
        firstName = dto.firstName.getOrElse(user.firstName),
        lastName  = dto.lastName.getOrElse(user.lastName),
        gender    = dto.gender.getOrElse(user.gender),
        birthDate = dto.birthDate.getOrElse(user.birthDate)
      )

      UserRepository.update(id, updatedUser).mapToUnit
    }

    def checkAndUpdate(users: Seq[User]): ServiceResult[Unit] = {
      users.find(_.id == id) match {
        case Some(user) ⇒
          users.find(_.email == dto.email) match {
            case Some(userSomeEmail) ⇒
              if (userSomeEmail.id == id) {
                updateAndSave(user)
              } else {
                async(error(400))
              }

            case None ⇒ updateAndSave(user)
          }

        case None ⇒ async(error(404))
      }
    }

    dto.email match {
      case Some(email) ⇒
        UserRepository.findByIdOrEmail(id, email).flatMap {
          case Nil   ⇒ async(error(404))
          case users ⇒ checkAndUpdate(users)
        }
      case None ⇒
        UserRepository.findOne(id).flatMap {
          case Some(user) ⇒ updateAndSave(user)
          case None       ⇒ async(error(404))
        }

    }
  }

  def findOne(id: Long): ServiceResult[User] = {
    UserRepository.findOne(id).map {
      case Some(v) ⇒ success(v)
      case None    ⇒ error(404)
    }
  }

}
