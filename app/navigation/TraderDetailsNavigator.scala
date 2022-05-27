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
import pages._
import pages.traderDetails.holderOfTransit._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class TraderDetailsNavigator @Inject() () extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case EoriYesNoPage              => ua => eoriYesNoRoute(ua, NormalMode)
    case EoriPage                   => ua => Some(hotRoutes.NameController.onPageLoad(ua.lrn, NormalMode))
    case TirIdentificationYesNoPage => ua => tirIdentificationYesNoRoute(ua, NormalMode)
    case TirIdentificationPage      => ua => Some(hotRoutes.NameController.onPageLoad(ua.lrn, NormalMode))
    case NamePage                   => ua => Some(hotRoutes.AddressController.onPageLoad(ua.lrn, NormalMode))
    case AddressPage                => ua => Some(hotRoutes.AddContactController.onPageLoad(ua.lrn, NormalMode))
    case AddContactPage             => ua => addContactRoute(ua, NormalMode)
    case ContactNamePage            => ua => Some(hotRoutes.ContactTelephoneNumberController.onPageLoad(ua.lrn, NormalMode))
    case ContactTelephoneNumberPage => ua => Some(hotRoutes.CheckYourAnswersController.onPageLoad(ua.lrn))
  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case EoriYesNoPage              => ua => eoriYesNoRoute(ua, CheckMode)
    case EoriPage                   => ua => Some(hotRoutes.CheckYourAnswersController.onPageLoad(ua.lrn))
    case TirIdentificationYesNoPage => ua => tirIdentificationYesNoRoute(ua, CheckMode)
    case TirIdentificationPage      => ua => Some(hotRoutes.CheckYourAnswersController.onPageLoad(ua.lrn))
    case NamePage                   => ua => Some(hotRoutes.CheckYourAnswersController.onPageLoad(ua.lrn))
    case AddressPage                => ua => Some(hotRoutes.CheckYourAnswersController.onPageLoad(ua.lrn))
    case AddContactPage             => ua => addContactRoute(ua, CheckMode)
    case ContactNamePage            => ua => contactNameRoute(ua, CheckMode)
    case ContactTelephoneNumberPage => ua => Some(hotRoutes.CheckYourAnswersController.onPageLoad(ua.lrn))
  }

  private def eoriYesNoRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] = Some {
    (userAnswers.get(EoriYesNoPage), mode) match {
      case (Some(true), mode)        => hotRoutes.EoriController.onPageLoad(userAnswers.lrn, mode)
      case (Some(false), NormalMode) => hotRoutes.NameController.onPageLoad(userAnswers.lrn, mode)
      case (Some(false), CheckMode)  => hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
      case (None, _)                 => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def tirIdentificationYesNoRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] = Some {
    (userAnswers.get(TirIdentificationYesNoPage), mode) match {
      case (Some(true), _)           => hotRoutes.TirIdentificationController.onPageLoad(userAnswers.lrn, mode)
      case (Some(false), NormalMode) => hotRoutes.NameController.onPageLoad(userAnswers.lrn, mode)
      case (Some(false), CheckMode)  => hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
      case (None, _)                 => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addContactRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] = Some {
    userAnswers.get(AddContactPage) match {
      case Some(true)  => hotRoutes.ContactNameController.onPageLoad(userAnswers.lrn, mode)
      case Some(false) => hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
      case None        => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def contactNameRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] = Some {
    userAnswers.get(ContactTelephoneNumberPage) match {
      case Some(_) => hotRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
      case None    => hotRoutes.ContactTelephoneNumberController.onPageLoad(userAnswers.lrn, CheckMode)
    }
  }

}
