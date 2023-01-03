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

package viewModels.transport.transportMeans.active

import config.FrontendAppConfig
import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.Content
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import utils.cyaHelpers.transport.transportMeans.active.ActiveBorderTransportsAnswersHelper
import viewModels.ListItem

import javax.inject.Inject

case class AddAnotherBorderTransportViewModel(
  listItems: Seq[ListItem],
  onSubmitCall: Call
) {

  val activeBorderTransports: Int = listItems.length
  val singularOrPlural: String    = if (activeBorderTransports == 1) "singular" else "plural"

  val prefix: String = "transport.transportMeans.active.addAnotherBorderTransport"

  def title(implicit messages: Messages): String         = messages(s"$prefix.$singularOrPlural.title", activeBorderTransports)
  def heading(implicit messages: Messages): String       = messages(s"$prefix.$singularOrPlural.heading", activeBorderTransports)
  def legend(implicit messages: Messages): String        = messages(s"$prefix.label")
  def maxLimitLabel(implicit messages: Messages): String = messages(s"$prefix.maxLimit.label")
  def hint(implicit messages: Messages): Content         = messages(s"$prefix.hint").toText

  def allowMoreActiveBorderTransports(implicit config: FrontendAppConfig): Boolean =
    activeBorderTransports < config.maxActiveBorderTransports
}

object AddAnotherBorderTransportViewModel {

  class AddAnotherBorderTransportViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): AddAnotherBorderTransportViewModel = {
      val helper = new ActiveBorderTransportsAnswersHelper(userAnswers, mode)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      new AddAnotherBorderTransportViewModel(
        listItems,
        onSubmitCall = controllers.transport.transportMeans.active.routes.AddAnotherBorderTransportController.onSubmit(userAnswers.lrn, mode)
      )
    }
  }
}
