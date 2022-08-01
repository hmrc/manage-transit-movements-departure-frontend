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

import models.domain.{GettableAsReaderOps, JsArrayGettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.{Index, RichJsArray}
import pages.routeDetails.transit.{AddOfficeOfTransitYesNoPage, OfficeOfTransitCountryPage, T2DeclarationTypeYesNoPage}
import pages.sections.routeDetails.OfficeOfTransitCountriesSection

case class TransitDomain(
  t2DeclarationType: Boolean,
  addOfficeOfTransit: Boolean,
  officesOfTransitCountries: Seq[OfficeOfTransitCountryDomain]
) extends JourneyDomainModel

object TransitDomain {

  private val officeOfTransitCountriesReader: UserAnswersReader[Seq[OfficeOfTransitCountryDomain]] =
    OfficeOfTransitCountriesSection.reader.flatMap {
      case x if x.isEmpty =>
        UserAnswersReader.fail[Seq[OfficeOfTransitCountryDomain]](OfficeOfTransitCountryPage(Index(0)))
      case x =>
        x.traverse[OfficeOfTransitCountryDomain](OfficeOfTransitCountryDomain.userAnswersReader).map(_.toSeq)
    }

  implicit val userAnswersReader: UserAnswersReader[TransitDomain] = {

    for {
      t2DeclarationType         <- T2DeclarationTypeYesNoPage.reader
      addOfficeOfTransit        <- AddOfficeOfTransitYesNoPage.reader
      officesOfTransitCountries <- officeOfTransitCountriesReader
    } yield TransitDomain(
      t2DeclarationType,
      addOfficeOfTransit,
      officesOfTransitCountries
    )
  }
}
