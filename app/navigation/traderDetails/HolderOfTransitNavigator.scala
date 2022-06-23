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

import controllers.traderDetails.holderOfTransit.contact.{routes => contactRoutes}
import controllers.traderDetails.holderOfTransit.{routes => hotRoutes}
import controllers.traderDetails.{routes => tdRoutes}
import models._
import models.journeyDomain.traderDetails.HolderOfTransitDomain
import navigation.UserAnswersNavigator
import pages.traderDetails.holderOfTransit._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class HolderOfTransitNavigator @Inject() () extends UserAnswersNavigator[HolderOfTransitDomain] {

  override def routes(mode: Mode): RouteMapping = {
    case EoriYesNoPage               => ua => hotRoutes.EoriYesNoController.onPageLoad(ua.lrn, mode)
    case EoriPage                    => ua => hotRoutes.EoriController.onPageLoad(ua.lrn, mode)
    case TirIdentificationYesNoPage  => ua => hotRoutes.TirIdentificationYesNoController.onPageLoad(ua.lrn, mode)
    case TirIdentificationPage       => ua => hotRoutes.TirIdentificationController.onPageLoad(ua.lrn, mode)
    case NamePage                    => ua => hotRoutes.NameController.onPageLoad(ua.lrn, mode)
    case AddressPage                 => ua => hotRoutes.AddressController.onPageLoad(ua.lrn, mode)
    case AddContactPage              => ua => hotRoutes.AddContactController.onPageLoad(ua.lrn, mode)
    case contact.NamePage            => ua => contactRoutes.NameController.onPageLoad(ua.lrn, mode)
    case contact.TelephoneNumberPage => ua => contactRoutes.TelephoneNumberController.onPageLoad(ua.lrn, mode)
  }

  override def checkYourAnswersRoute(mode: Mode, userAnswers: UserAnswers): Call =
    mode match {
      case NormalMode => hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
      case CheckMode  => tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
    }

}
