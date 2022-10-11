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

package utils.cyaHelpers.routeDetails.routing

import controllers.routeDetails.routing.index.routes
import models.journeyDomain.routeDetails.routing.CountryOfRoutingDomain
import models.reference.{Country, CustomsOffice}
import models.{Index, Mode, UserAnswers}
import pages.routeDetails.routing._
import pages.routeDetails.routing.index.CountryOfRoutingPage
import pages.sections.routeDetails.routing.CountriesOfRoutingSection
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class RoutingCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def countryOfDestination: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryOfDestinationPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.routing.countryOfDestination",
    id = Some("country-of-destination")
  )

  def officeOfDestination: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = OfficeOfDestinationPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.routing.officeOfDestination",
    id = Some("office-of-destination")
  )

  def bindingItinerary: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = BindingItineraryPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.routing.bindingItinerary",
    id = Some("binding-itinerary")
  )

  def addCountryOfRouting: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddCountryOfRoutingYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.routing.addCountryOfRoutingYesNo",
    id = Some("add-country-of-routing")
  )

  def countryOfRouting(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[CountryOfRoutingDomain, Country](
    page = CountryOfRoutingPage(index),
    formatAnswer = formatAsText,
    prefix = "routeDetails.checkYourAnswers.routing.countryOfRouting",
    id = Some(s"change-country-of-routing-${index.display}"),
    args = index.display
  )(CountryOfRoutingDomain.userAnswersReader(index), implicitly)

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(CountriesOfRoutingSection) {
      position =>
        val index = Index(position)
        buildListItem[CountryOfRoutingDomain, Country](
          page = CountryOfRoutingPage(index),
          formatJourneyDomainModel = _.country.toString,
          formatType = _.toString,
          removeRoute = Some(routes.RemoveCountryOfRoutingYesNoController.onPageLoad(userAnswers.lrn, mode, index))
        )(CountryOfRoutingDomain.userAnswersReader(index), implicitly)
    }
}