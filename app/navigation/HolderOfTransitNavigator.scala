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

import controllers.traderDetails.holderOfTransit.{routes => hotRoutes}
import models._
import models.domain.UserAnswersReader
import models.journeyDomain.traderDetails.HolderOfTransitDomain
import pages._
import pages.traderDetails.holderOfTransit._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class HolderOfTransitNavigator @Inject() () extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = routes(NormalMode)

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case page =>
      ua =>
        UserAnswersReader[HolderOfTransitDomain].run(ua) match {
          case Left(_)  => routes(CheckMode).applyOrElse[Page, UserAnswers => Option[Call]](page, _ => _ => None)(ua)
          case Right(_) => Some(hotRoutes.CheckYourAnswersController.onPageLoad(ua.lrn))
        }
  }

  private def routes(mode: Mode): PartialFunction[Page, UserAnswers => Option[Call]] = {
    case EoriYesNoPage              => ua => eoriYesNoRoute(ua, mode)
    case EoriPage                   => ua => Some(hotRoutes.NameController.onPageLoad(ua.lrn, mode))
    case TirIdentificationYesNoPage => ua => tirIdentificationYesNoRoute(ua, mode)
    case TirIdentificationPage      => ua => Some(hotRoutes.NameController.onPageLoad(ua.lrn, mode))
    case NamePage                   => ua => Some(hotRoutes.AddressController.onPageLoad(ua.lrn, mode))
    case AddressPage                => ua => Some(hotRoutes.AddContactController.onPageLoad(ua.lrn, mode))
    case AddContactPage             => ua => addContactRoute(ua, mode)
    case ContactNamePage            => ua => Some(hotRoutes.ContactTelephoneNumberController.onPageLoad(ua.lrn, mode))
    case ContactTelephoneNumberPage => ua => Some(hotRoutes.CheckYourAnswersController.onPageLoad(ua.lrn))
  }

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
      yesCall = hotRoutes.ContactNameController.onPageLoad(userAnswers.lrn, mode)
    )(
      noCall = hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
    )

}
