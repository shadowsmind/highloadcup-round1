package com.shadowsmind.api.validation

import com.shadowsmind.models.{ Visit, VisitUpdateDto }

object VisitCreateValidator {

  def validate(visit: Visit): Boolean = {
    (visit.mark >= 0) &&
      (visit.mark <= 5)
  }

}

object VisitUpdateValidator {

  def validate(visit: VisitUpdateDto): Boolean = {
    visit.mark.fold(true)(m ⇒ (m >= 0) && (m <= 5))
  }

}
