/*
 * Copyright 2023 HM Revenue & Customs
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

package models.journeyDomain.routeDetails.routing

import cats.implicits._
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{GettableAsReaderOps, JsArrayGettableAsReaderOps, UserAnswersReader}
import models.{Index, RichJsArray}
import pages.preTaskList.SecurityDetailsTypePage
import pages.routeDetails.routing._
import pages.sections.routeDetails.routing.CountriesOfRoutingSection

object CountriesOfRoutingDomain {

  implicit val userAnswersReader: UserAnswersReader[Seq[CountryOfRoutingDomain]] = {
    val arrayReader: UserAnswersReader[Seq[CountryOfRoutingDomain]] = CountriesOfRoutingSection.arrayReader.flatMap {
      case x if x.isEmpty =>
        UserAnswersReader[CountryOfRoutingDomain](CountryOfRoutingDomain.userAnswersReader(Index(0))).map(Seq(_))
      case x =>
        x.traverse[CountryOfRoutingDomain](CountryOfRoutingDomain.userAnswersReader)
    }

    for {
      securityDetailsType       <- SecurityDetailsTypePage.reader
      followingBindingItinerary <- BindingItineraryPage.reader
      reader <- (securityDetailsType, followingBindingItinerary) match {
        case (NoSecurityDetails, false) =>
          AddCountryOfRoutingYesNoPage.reader.flatMap {
            case true  => arrayReader
            case false => UserAnswersReader(Seq.empty[CountryOfRoutingDomain])
          }
        case _ => arrayReader
      }
    } yield reader
  }
}
