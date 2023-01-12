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

package viewModels.transport.authorisations

import config.FrontendAppConfig
import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.transport.authorisations.AuthorisationsAnswersHelper
import viewModels.ListItem

import javax.inject.Inject

case class AddAnotherAuthorisationViewModel(
  listItems: Seq[ListItem],
  onSubmitCall: Call
) {

  val authorisations: Int      = listItems.length
  val singularOrPlural: String = if (authorisations == 1) "singular" else "plural"

  val prefix: String = "transport.authorisations.addAnotherAuthorisation"

  def title(implicit messages: Messages): String         = messages(s"$prefix.$singularOrPlural.title", authorisations)
  def heading(implicit messages: Messages): String       = messages(s"$prefix.$singularOrPlural.heading", authorisations)
  def legend(implicit messages: Messages): String        = messages(s"$prefix.label")
  def maxLimitLabel(implicit messages: Messages): String = messages(s"$prefix.maxLimit.label")

  def allowMoreAuthorisations(implicit config: FrontendAppConfig): Boolean =
    authorisations < config.maxAuthorisations
}

object AddAnotherAuthorisationViewModel {

  class AddAnotherAuthorisationViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): AddAnotherAuthorisationViewModel = {
      val helper = new AuthorisationsAnswersHelper(userAnswers, mode)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      new AddAnotherAuthorisationViewModel(
        listItems,
        onSubmitCall = controllers.transport.authorisationsAndLimit.authorisations.routes.AddAnotherAuthorisationController.onSubmit(userAnswers.lrn, mode)
      )
    }
  }
}
