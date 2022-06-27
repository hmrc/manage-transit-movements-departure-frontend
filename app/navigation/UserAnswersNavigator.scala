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
import models.journeyDomain.ReaderError
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import play.api.mvc.Call

abstract class UserAnswersNavigator[SubSection, Section](implicit
  subSectionReader: UserAnswersReader[SubSection],
  sectionReader: UserAnswersReader[Section]
) extends Navigator {

  def subSectionCheckYourAnswersRoute(userAnswers: UserAnswers): Call = sectionCheckYourAnswersRoute(userAnswers)

  def sectionCheckYourAnswersRoute(userAnswers: UserAnswers): Call

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
    mode match {
      case NormalMode => nextPage[SubSection](userAnswers, mode, subSectionCheckYourAnswersRoute)
      case CheckMode  => nextPage[Section](userAnswers, mode, sectionCheckYourAnswersRoute)
    }

  private def nextPage[A](
    userAnswers: UserAnswers,
    mode: Mode,
    route: UserAnswers => Call
  )(implicit userAnswersReader: UserAnswersReader[A]): Call =
    UserAnswersReader[A].run(userAnswers) match {
      case Left(ReaderError(page, _)) =>
        page.route(userAnswers, mode).getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
      case Right(_) =>
        route(userAnswers)
    }
}
