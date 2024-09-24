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

package utils.cyaHelpers

import models.{LocalReferenceNumber, Mode, UserAnswers}
import pages.QuestionPage
import play.api.i18n.Messages
import play.api.libs.json.Reads
import uk.gov.hmrc.govukfrontend.views.html.components.{Content, SummaryListRow}

class AnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends SummaryListRowHelper {

  protected def lrn: LocalReferenceNumber = userAnswers.lrn

  protected def getAnswerAndBuildRow[T](
    page: QuestionPage[T],
    formatAnswer: T => Content,
    prefix: String,
    id: Option[String],
    args: Any*
  )(implicit rds: Reads[T]): Option[SummaryListRow] =
    for {
      answer <- userAnswers.get(page)
      call   <- page.route(userAnswers, mode)
    } yield buildRow(
      prefix = prefix,
      answer = formatAnswer(answer),
      id = id,
      call = call,
      args = args*
    )

}
