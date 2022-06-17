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

import models._
import models.journeyDomain.traderDetails.ConsignmentDomain
import pages.traderDetails.consignment._
import controllers.traderDetails.consignment.consignor.{routes => consignorRoutes}
import controllers.traderDetails.consignment.consignor.contact.{routes => contactRoutes}
import controllers.traderDetails.consignment.consignee.{routes => consigneeRoutes}
import models.SecurityDetailsType.NoSecurityDetails
import pages.preTaskList.SecurityDetailsTypePage
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class ConsignmentNavigator @Inject() () extends TraderDetailsNavigator[ConsignmentDomain] {

  override def routes(mode: Mode): RouteMapping = {
    case ApprovedOperatorPage                  => ua => approvedOperatorYesNoRoute(ua, mode)
    case consignor.EoriYesNoPage               => ua => consignorEoriYesNoRoute(ua, mode)
    case consignor.EoriPage                    => ua => Some(consignorRoutes.NameController.onPageLoad(ua.lrn, mode))
    case consignor.NamePage                    => ua => Some(consignorRoutes.AddressController.onPageLoad(ua.lrn, mode))
    case consignor.AddressPage                 => ua => Some(consignorRoutes.AddContactController.onPageLoad(ua.lrn, mode))
    case consignor.AddContactPage              => ua => addContactRoute(ua, mode)
    case consignor.contact.NamePage            => ua => Some(contactRoutes.TelephoneNumberController.onPageLoad(ua.lrn, mode))
    case consignor.contact.TelephoneNumberPage => ua => Some(consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(ua.lrn, mode))
    case consignee.MoreThanOneConsigneePage    => ua => consigneeEoriYesNoRoute(ua, mode)
  }

  private def approvedOperatorYesNoRoute(ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(SecurityDetailsTypePage), ua.get(ApprovedOperatorPage)) match {
      case (Some(NoSecurityDetails), Some(true)) => Some(consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(ua.lrn, mode))
      case (Some(_), Some(_))                    => Some(consignorRoutes.EoriYesNoController.onPageLoad(ua.lrn, mode))
      case _                                     => Some(controllers.routes.SessionExpiredController.onPageLoad())
    }

  private def consignorEoriYesNoRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, consignor.EoriYesNoPage)(
      yesCall = consignorRoutes.EoriController.onPageLoad(userAnswers.lrn, mode)
    )(
      noCall = consignorRoutes.NameController.onPageLoad(userAnswers.lrn, mode)
    )

  private def addContactRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, consignor.AddContactPage)(
      yesCall = contactRoutes.NameController.onPageLoad(userAnswers.lrn, mode)
    )(
      noCall = consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(userAnswers.lrn, mode)
    )

  private def consigneeEoriYesNoRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, consignee.MoreThanOneConsigneePage)(
      yesCall = consigneeRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode) //TODO change this to check your answers when built
    )(
      noCall = consigneeRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode)
    )

  override def checkYourAnswersRoute(userAnswers: UserAnswers): Call =
    ??? //todo update when new pages built consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
}
