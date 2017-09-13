package com.shadowsmind.api.validation

import com.shadowsmind.models.{ Location, LocationUpdateDto }

object LocationCreateValidator {

  def validate(location: Location): Boolean = {
    (location.country.length <= 50) &&
      (location.city.length <= 50)
  }

}

object LocationUpdateValidator {

  def validate(location: LocationUpdateDto): Boolean = {
    location.country.fold(true)(_.length <= 50) &&
      location.city.fold(true)(_.length <= 50)
  }

}