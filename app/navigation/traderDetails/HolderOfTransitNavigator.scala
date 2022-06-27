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

package navigation.traderDetails

import controllers.traderDetails.holderOfTransit.{routes => hotRoutes}
import controllers.traderDetails.{routes => tdRoutes}
import models._
import models.journeyDomain.traderDetails.{HolderOfTransitDomain, TraderDetailsDomain}
import navigation.UserAnswersNavigator
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class HolderOfTransitNavigator @Inject() () extends UserAnswersNavigator[HolderOfTransitDomain, TraderDetailsDomain] {

  override def subSectionCheckYourAnswersRoute(userAnswers: UserAnswers): Call =
    hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)

  override def sectionCheckYourAnswersRoute(userAnswers: UserAnswers): Call =
    tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)

}
