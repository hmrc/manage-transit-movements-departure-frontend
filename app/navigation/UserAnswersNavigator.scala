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

package navigation

import models.domain.UserAnswersReader
import models.journeyDomain.{CheckYourAnswersDomain, ReaderError}
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import play.api.mvc.Call

abstract class UserAnswersNavigator[A <: CheckYourAnswersDomain, B <: CheckYourAnswersDomain](implicit
  subSectionReader: UserAnswersReader[A],
  sectionReader: UserAnswersReader[B]
) extends Navigator {

  type SubSection = A
  type Section    = B

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
    mode match {
      case NormalMode => nextPage[SubSection](userAnswers, mode)
      case CheckMode  => nextPage[Section](userAnswers, mode)
    }

  private def nextPage[T <: CheckYourAnswersDomain](
    userAnswers: UserAnswers,
    mode: Mode
  )(implicit userAnswersReader: UserAnswersReader[T]): Call =
    UserAnswersReader[T].run(userAnswers) match {
      case Left(ReaderError(page, _)) =>
        page.route(userAnswers, mode).getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
      case Right(x) =>
        x.checkYourAnswersRoute(userAnswers)
    }
}
