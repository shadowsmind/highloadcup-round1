package com.shadowsmind.api.validation

import com.shadowsmind.models.{ User, UserUpdateDto }

object UserCreateValidator {

  def validate(user: User): Boolean = {
    (user.email.length <= 100) &&
      (user.firstName.length <= 50) &&
      (user.lastName.length <= 50)
  }

}

object UserUpdateValidator {

  def validate(user: UserUpdateDto): Boolean = {
    user.email.fold(true)(_.length <= 100) &&
      user.firstName.fold(true)(_.length <= 50) &&
      user.lastName.fold(true)(_.length <= 50)
  }

}