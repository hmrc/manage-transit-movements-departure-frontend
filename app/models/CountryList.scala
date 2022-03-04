/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import models.reference.{Country, CountryCode}

case class CountryList(countries: Seq[Country]) {
  def getCountry(countryCode: CountryCode): Option[Country] = countries.find(_.code == countryCode)

  override def equals(obj: Any): Boolean =
    obj match {
      case value: CountryList => value.countries == countries
      case _                  => false
    }
}

object CountryList {
  def apply(countries: Seq[Country]): CountryList = new CountryList(countries)
}
