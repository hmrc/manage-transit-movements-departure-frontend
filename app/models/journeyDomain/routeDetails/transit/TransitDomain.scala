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

package models.journeyDomain.routeDetails.transit

import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JsArrayGettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.{Index, RichJsArray, UserAnswers}
import pages.routeDetails.transit.index.OfficeOfTransitCountryPage
import pages.routeDetails.transit.{AddOfficeOfTransitYesNoPage, T2DeclarationTypeYesNoPage}
import pages.sections.routeDetails.OfficeOfTransitCountriesSection
import play.api.mvc.Call

case class TransitDomain(
  t2DeclarationType: Boolean,
  officesOfTransit: Seq[OfficeOfTransitDomain]
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] =
    Some(controllers.routes.TaskListController.onPageLoad(userAnswers.lrn))
}

object TransitDomain {

  private val officeOfTransitCountriesReader: UserAnswersReader[Seq[OfficeOfTransitDomain]] =
    OfficeOfTransitCountriesSection.reader.flatMap {
      case x if x.isEmpty =>
        UserAnswersReader.fail[Seq[OfficeOfTransitDomain]](OfficeOfTransitCountryPage(Index(0)))
      case x =>
        x.traverse[OfficeOfTransitDomain](OfficeOfTransitDomain.userAnswersReader).map(_.toSeq)
    }

  implicit val userAnswersReader: UserAnswersReader[TransitDomain] = {

    for {
      t2DeclarationType         <- T2DeclarationTypeYesNoPage.reader
      officesOfTransitCountries <- AddOfficeOfTransitYesNoPage.filterOptionalDependent(identity)(officeOfTransitCountriesReader)
    } yield TransitDomain(
      t2DeclarationType,
      officesOfTransitCountries.getOrElse(Nil)
    )
  }
}
