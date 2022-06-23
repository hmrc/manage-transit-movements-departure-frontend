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
import models.{Mode, UserAnswers}
import pages.Page
import play.api.mvc.Call

abstract class UserAnswersNavigator[T](implicit userAnswersReader: UserAnswersReader[T]) extends Navigator {

  def checkYourAnswersRoute(mode: Mode, userAnswers: UserAnswers): Call

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
    UserAnswersReader[T].run(userAnswers) match {
      case Left(ReaderError(page, _)) =>
        routes(mode)
          .applyOrElse[Page, Route](page, _ => _ => controllers.routes.SessionExpiredController.onPageLoad())
          .apply(userAnswers)
      case Right(_) => checkYourAnswersRoute(mode, userAnswers)
    }
}
