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

package pages.routeDetails.loadingAndUnloading.loading

import controllers.routeDetails.loadingAndUnloading.loading.routes
import models.reference.UnLocode
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.routeDetails.loading.LoadingSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object PlaceOfLoadingUnLocodePage extends QuestionPage[UnLocode] {

  override def path: JsPath = LoadingSection.path \ toString

  override def toString: String = "unLocode"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.PlaceOfLoadingUnLocodeController.onPageLoad(userAnswers.lrn, mode))
}
