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

package utils.cyaHelpers.test

import models.{Index, Mode, UserAnswers}
import pages.test._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class TestCheckYourAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  fooIndex: Index,
  barIndex: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def test1: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = Test1Page(fooIndex, barIndex),
    formatAnswer = formatAsLiteral,
    prefix = "test.test1",
    id = None
  )

  def test2: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = Test2Page(fooIndex, barIndex),
    formatAnswer = formatAsLiteral,
    prefix = "test.test2",
    id = None
  )
}
