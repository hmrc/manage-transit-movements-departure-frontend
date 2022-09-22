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

package models.journeyDomain.routeDetails.loadingAndUnloading.unloading

import cats.implicits._
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.reference.UnLocode
import pages.routeDetails.loadingAndUnloading.unloading._

case class UnloadingDomain(
  unLocode: Option[UnLocode],
  additionalInformation: Option[AdditionalInformationDomain]
) extends JourneyDomainModel

object UnloadingDomain {

  implicit val userAnswersReader: UserAnswersReader[UnloadingDomain] = {

    implicit val unLocodeReads: UserAnswersReader[Option[UnLocode]] =
      PlaceOfUnloadingUnLocodeYesNoPage.filterOptionalDependent(identity)(PlaceOfUnloadingUnLocodePage.reader)

    implicit val additionalInformationReads: UserAnswersReader[Option[AdditionalInformationDomain]] =
      PlaceOfUnloadingUnLocodeYesNoPage.reader.flatMap {
        case true  => AddExtraInformationYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[AdditionalInformationDomain])
        case false => UserAnswersReader[AdditionalInformationDomain].map(Some(_))
      }

    (
      UserAnswersReader[Option[UnLocode]],
      UserAnswersReader[Option[AdditionalInformationDomain]]
    ).tupled.map((UnloadingDomain.apply _).tupled)
  }
}
