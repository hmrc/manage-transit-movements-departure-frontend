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
import models._
import models.journeyDomain.traderDetails.HolderOfTransitDomain
import pages.traderDetails.holderOfTransit._
import pages.traderDetails.holderOfTransit.contact
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class HolderOfTransitNavigator @Inject() () extends TraderDetailsNavigator[HolderOfTransitDomain] {

  override def routes(mode: Mode): RouteMapping = {
    case EoriYesNoPage               => ua => eoriYesNoRoute(ua, mode)
    case EoriPage                    => ua => Some(hotRoutes.NameController.onPageLoad(ua.lrn, mode))
    case TirIdentificationYesNoPage  => ua => tirIdentificationYesNoRoute(ua, mode)
    case TirIdentificationPage       => ua => Some(hotRoutes.NameController.onPageLoad(ua.lrn, mode))
    case NamePage                    => ua => Some(hotRoutes.AddressController.onPageLoad(ua.lrn, mode))
    case AddressPage                 => ua => Some(hotRoutes.AddContactController.onPageLoad(ua.lrn, mode))
    case AddContactPage              => ua => addContactRoute(ua, mode)
    case contact.NamePage            => ua => Some(contactRoutes.TelephoneNumberController.onPageLoad(ua.lrn, mode))
    case contact.TelephoneNumberPage => ua => Some(checkYourAnswersRoute(ua))
  }

  override def checkYourAnswersRoute(userAnswers: UserAnswers): Call =
    hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)

  private def eoriYesNoRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, EoriYesNoPage)(
      yesCall = hotRoutes.EoriController.onPageLoad(userAnswers.lrn, mode)
    )(
      noCall = hotRoutes.NameController.onPageLoad(userAnswers.lrn, mode)
    )

  private def tirIdentificationYesNoRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, TirIdentificationYesNoPage)(
      yesCall = hotRoutes.TirIdentificationController.onPageLoad(userAnswers.lrn, mode)
    )(
      noCall = hotRoutes.NameController.onPageLoad(userAnswers.lrn, mode)
    )

  private def addContactRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, AddContactPage)(
      yesCall = contactRoutes.NameController.onPageLoad(userAnswers.lrn, mode)
    )(
      noCall = checkYourAnswersRoute(userAnswers)
    )

}
