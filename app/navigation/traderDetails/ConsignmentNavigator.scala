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
import controllers.traderDetails.{routes => tdRoutes}
import models._
import models.journeyDomain.traderDetails.ConsignmentDomain
import navigation.UserAnswersNavigator
import pages.traderDetails.consignment._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class ConsignmentNavigator @Inject() () extends UserAnswersNavigator[ConsignmentDomain] {

  override def routes(mode: Mode): RouteMapping = {
    case ApprovedOperatorPage                  => ua => consignmentRoutes.ApprovedOperatorController.onPageLoad(ua.lrn, mode)
    case consignor.EoriYesNoPage               => ua => consignorRoutes.EoriYesNoController.onPageLoad(ua.lrn, mode)
    case consignor.EoriPage                    => ua => consignorRoutes.EoriController.onPageLoad(ua.lrn, mode)
    case consignor.NamePage                    => ua => consignorRoutes.NameController.onPageLoad(ua.lrn, mode)
    case consignor.AddressPage                 => ua => consignorRoutes.AddressController.onPageLoad(ua.lrn, mode)
    case consignor.AddContactPage              => ua => consignorRoutes.AddContactController.onPageLoad(ua.lrn, mode)
    case consignor.contact.NamePage            => ua => contactRoutes.NameController.onPageLoad(ua.lrn, mode)
    case consignor.contact.TelephoneNumberPage => ua => contactRoutes.TelephoneNumberController.onPageLoad(ua.lrn, mode)
    case consignee.MoreThanOneConsigneePage    => ua => consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(ua.lrn, mode)
    case consignee.EoriYesNoPage               => ua => consigneeRoutes.EoriYesNoController.onPageLoad(ua.lrn, mode)
    case consignee.EoriNumberPage              => ua => consigneeRoutes.EoriNumberController.onPageLoad(ua.lrn, mode)
    case consignee.NamePage                    => ua => consigneeRoutes.NameController.onPageLoad(ua.lrn, mode)
    case consignee.AddressPage                 => ua => consigneeRoutes.AddressController.onPageLoad(ua.lrn, mode)
  }

  override def checkYourAnswersRoute(mode: Mode, userAnswers: UserAnswers): Call =
    mode match {
      case NormalMode => consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
      case CheckMode  => tdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
    }

}
