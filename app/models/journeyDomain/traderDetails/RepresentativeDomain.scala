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

package models.journeyDomain.traderDetails

import cats.implicits._
import models.EoriNumber
import models.domain._
import models.traderDetails.representative.RepresentativeCapacity
import pages.traderDetails.representative._

case class RepresentativeDomain(
  eori: EoriNumber,
  name: String,
  capacity: RepresentativeCapacity,
  phone: String
)

object RepresentativeDomain {

  implicit val userAnswersReader: UserAnswersReader[RepresentativeDomain] =
    (
      RepresentativeEoriPage.reader.map(EoriNumber(_)),
      RepresentativeNamePage.reader,
      RepresentativeCapacityPage.reader,
      RepresentativePhonePage.reader
    ).tupled.map((RepresentativeDomain.apply _).tupled)
}
