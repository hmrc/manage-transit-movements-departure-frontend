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

import controllers.traderDetails.consignment.consignee.{routes => consigneeRoutes}
import controllers.traderDetails.consignment.consignor.contact.{routes => contactRoutes}
import controllers.traderDetails.consignment.consignor.{routes => consignorRoutes}
import controllers.traderDetails.consignment.{routes => consignmentRoutes}
import javax.inject.{Inject, Singleton}
import models._
import models.journeyDomain.traderDetails.ConsignmentDomain
import pages.traderDetails.consignment._
import play.api.mvc.Call

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
    case consignor.contact.TelephoneNumberPage => ua => Some(consignmentRoutes.MoreThanOneConsigneeController.onPageLoad(ua.lrn, mode))
    case MoreThanOneConsigneePage              => ua => moreThanOneConsigneeRoute(ua, mode)
    case consignee.EoriYesNoPage               => ua => consigneeEoriYesNoRoute(ua, mode)
    case consignee.EoriNumberPage              => ua => Some(consigneeRoutes.NameController.onPageLoad(ua.lrn, mode))
    case consignee.NamePage                    => ua => Some(consigneeRoutes.AddressController.onPageLoad(ua.lrn, mode))
    case consignee.AddressPage                 => ua => Some(checkYourAnswersRoute(ua))
  }

  private def approvedOperatorYesNoRoute(ua: UserAnswers, mode: Mode): Option[Call] =
    ApprovedOperatorPage.skipConsignor(ua) match {
      case Some(true)  => Some(consignmentRoutes.MoreThanOneConsigneeController.onPageLoad(ua.lrn, mode))
      case Some(false) => Some(consignorRoutes.EoriYesNoController.onPageLoad(ua.lrn, mode))
      case None        => Some(controllers.routes.SessionExpiredController.onPageLoad())
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
      noCall = consignmentRoutes.MoreThanOneConsigneeController.onPageLoad(userAnswers.lrn, mode)
    )

  private def moreThanOneConsigneeRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, MoreThanOneConsigneePage)(
      yesCall = checkYourAnswersRoute(userAnswers)
    )(
      noCall = consigneeRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode)
    )

  private def consigneeEoriYesNoRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, consignee.EoriYesNoPage)(
      yesCall = consigneeRoutes.EoriNumberController.onPageLoad(userAnswers.lrn, mode)
    )(
      noCall = consigneeRoutes.NameController.onPageLoad(userAnswers.lrn, mode)
    )

  override def checkYourAnswersRoute(userAnswers: UserAnswers): Call =
    consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
}
