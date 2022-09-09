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

package models.journeyDomain.routeDetails.locationOfGoods

import cats.implicits._
import models.domain._
import models.journeyDomain.JourneyDomainModel
import pages.routeDetails.locationOfGoods.LocationOfGoodsContactNamePage

case class AdditionalContactDomain(
  name: String,
  telephoneNumber: String
) extends JourneyDomainModel

object AdditionalContactDomain {

  implicit val userAnswersReader: UserAnswersReader[AdditionalContactDomain] =
    (
      LocationOfGoodsContactNamePage.reader,
      UserAnswersReader("") // TODO: Update with contact phoneNumber page reader when built
    ).tupled.map((AdditionalContactDomain.apply _).tupled)
}
