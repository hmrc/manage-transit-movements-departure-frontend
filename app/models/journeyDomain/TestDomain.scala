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

package models.journeyDomain

import cats.implicits._
import models.domain.{UserAnswersReader, _}
import models.{Index, UserAnswers}
import pages.test.{Test1Page, Test2Page}
import play.api.mvc.Call

case class TestDomain(
  test1: String,
  test2: String
)(fooIndex: Index, barIndex: Index)
    extends CheckYourAnswersDomain {

  override def checkYourAnswersRoute(userAnswers: UserAnswers): Call =
    controllers.test.routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn, fooIndex, barIndex)
}

object TestDomain {

  implicit def userAnswersReader(fooIndex: Index, barIndex: Index): UserAnswersReader[TestDomain] =
    (
      Test1Page(fooIndex, barIndex).reader,
      Test2Page(fooIndex, barIndex).reader
    ).mapN(
      (x, y) => TestDomain.apply(x, y)(fooIndex, barIndex)
    )
}
