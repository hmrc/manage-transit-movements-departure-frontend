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
import models.journeyDomain.traderDetails.RepresentativeDomain
import pages.traderDetails.consignment._
import controllers.traderDetails.consignment.consignor.{routes => consignorRoutes}
import play.api.mvc.Call
import javax.inject.{Inject, Singleton}

@Singleton
class ConsignmentNavigator @Inject() () extends TraderDetailsNavigator[RepresentativeDomain] {

  override def routes(mode: Mode): RouteMapping = {
    case ApprovedOperatorPage     => ua => approvedOperatorYesNoRoute(ua, mode)
    case consignor.EoriYesNoPage  => ua => consignorEoriYesNoRoute(ua, mode)
    case consignor.EoriPage       => ua => Some(consignorRoutes.NameController.onPageLoad(ua.lrn, mode))
    case consignor.NamePage       => ua => Some(consignorRoutes.AddressController.onPageLoad(ua.lrn, mode))
    case consignor.AddressPage    => ua => Some(consignorRoutes.AddContactController.onPageLoad(ua.lrn, mode))
    case consignor.AddContactPage => ua => addContactRoute(ua, mode)
  }

  private def approvedOperatorYesNoRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, ApprovedOperatorPage)(
      yesCall = consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode)
    )(
      noCall = consignorRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn,
                                                              mode
      ) //TODO replace with consigneeRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, mode)
    )

  private def consignorEoriYesNoRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(userAnswers, consignor.EoriYesNoPage)(
      yesCall = consignorRoutes.EoriController.onPageLoad(userAnswers.lrn, mode)
    )(
      noCall = consignorRoutes.NameController.onPageLoad(userAnswers.lrn, mode)
    )

  private def addContactRoute(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    ??? //todo update when new pages built

  override def checkYourAnswersRoute(userAnswers: UserAnswers): Call =
    ??? //todo update when new pages built consignmentRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn)
}
