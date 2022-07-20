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

package models.journeyDomain.routeDetails

import cats.implicits._
import models.domain.{GettableAsReaderOps, JsArrayGettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.{Index, RichJsArray, UserAnswers}
import pages.routeDetails.routing.{AddCountryOfRoutingYesNoPage, BindingItineraryPage, CountryOfRoutingPage}
import pages.sections.routeDetails.CountriesOfRoutingSection
import play.api.mvc.Call

case class RoutingDomain(
  bindingItinerary: Boolean,
  countriesOfRouting: Seq[CountryOfRoutingDomain]
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] =
    None
}

object RoutingDomain {

  private val countriesOfRoutingReader: UserAnswersReader[Seq[CountryOfRoutingDomain]] =
    AddCountryOfRoutingYesNoPage.reader.flatMap {
      case true =>
        CountriesOfRoutingSection.reader.flatMap {
          case x if x.isEmpty =>
            UserAnswersReader.fail[Seq[CountryOfRoutingDomain]](CountryOfRoutingPage(Index(0)))
          case x =>
            x.traverse[CountryOfRoutingDomain](CountryOfRoutingDomain.userAnswersReader).map(_.toSeq)
        }
      case false =>
        UserAnswersReader(Nil)
    }

  implicit val userAnswersReader: UserAnswersReader[RoutingDomain] =
    (
      BindingItineraryPage.reader,
      countriesOfRoutingReader
    ).tupled.map((RoutingDomain.apply _).tupled)
}
