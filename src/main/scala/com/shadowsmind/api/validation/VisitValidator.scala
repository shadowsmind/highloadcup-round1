package com.shadowsmind.api.validation

import com.shadowsmind.models.VisitFields

object VisitValidator {

  def validate(visit: VisitFields): Boolean = {
    (visit.mark >= 0) &&
      (visit.mark <= 5)
  }

}
