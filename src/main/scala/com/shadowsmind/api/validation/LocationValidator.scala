package com.shadowsmind.api.validation

import com.shadowsmind.models.LocationFields

object LocationValidator {

  def validate(location: LocationFields): Boolean = {
    (location.country.length <= 50) &&
      (location.city.length <= 50)
  }

}
