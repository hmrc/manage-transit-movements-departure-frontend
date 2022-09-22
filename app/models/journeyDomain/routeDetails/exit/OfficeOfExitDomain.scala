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

package models.journeyDomain.routeDetails.exit

import cats.implicits._
import controllers.routeDetails.exit.index.routes
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.{Country, CustomsOffice}
import models.{Index, Mode, UserAnswers}
import pages.routeDetails.exit.index.{OfficeOfExitCountryPage, OfficeOfExitPage}
import play.api.mvc.Call

case class OfficeOfExitDomain(
  country: Country,
  customsOffice: CustomsOffice
)(index: Index)
    extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(routes.CheckOfficeOfExitAnswersController.onPageLoad(userAnswers.lrn, index, mode))

  val label: String = s"$country - $customsOffice"
}

object OfficeOfExitDomain {

  implicit def userAnswersReader(
    index: Index
  ): UserAnswersReader[OfficeOfExitDomain] =
    (
      OfficeOfExitCountryPage(index).reader,
      OfficeOfExitPage(index).reader
    ).mapN {
      (country, office) => OfficeOfExitDomain(country, office)(index)
    }
}
