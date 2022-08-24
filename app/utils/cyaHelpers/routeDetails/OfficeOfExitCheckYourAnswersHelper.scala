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

package utils.cyaHelpers.routeDetails

import models.reference.Country
import models.{Index, Mode, UserAnswers}
import pages.routeDetails.officeOfExit.OfficeOfExitCountryPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class OfficeOfExitCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode, index: Index)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def officeOfExitCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = OfficeOfExitCountryPage(index),
    formatAnswer = formatAsText,
    prefix = "routeDetails.officeOfExit.officeOfExitCountry",
    id = Some("office-of-exit-country")
  )

}
