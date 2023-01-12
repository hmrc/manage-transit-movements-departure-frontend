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

package utils.cyaHelpers.transport.authorisations

import models.journeyDomain.transport.AuthorisationDomain
import models.{Mode, UserAnswers}
import pages.sections.transport.AuthorisationsSection
import pages.transport.authorisation.index.AuthorisationTypePage
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.GET
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class AuthorisationsAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(AuthorisationsSection) {
      index =>
        val removeRoute: Option[Call] = if (userAnswers.get(AuthorisationTypePage(index)).isEmpty && index.isFirst) {
          None
        } else {
          Some(Call(GET, "#")) //TODO: Add ConfirmRemoveAuthorisation when created
        }

        buildListItem[AuthorisationDomain](
          nameWhenComplete = _.asString(),
          nameWhenInProgress = userAnswers.get(AuthorisationTypePage(index)).map(_.asString),
          removeRoute = removeRoute
        )(AuthorisationDomain.userAnswersReader(index))
    }

}