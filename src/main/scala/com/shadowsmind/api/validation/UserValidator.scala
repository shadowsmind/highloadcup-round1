package com.shadowsmind.api.validation

import com.shadowsmind.models.UserFields

object UserValidator {

  def validate(user: UserFields): Boolean = {
    (user.email.length <= 100) &&
      (user.firstName.length <= 50) &&
      (user.lastName.length <= 50)
  }

}