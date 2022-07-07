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

package models.journeyDomain.traderDetails.consignment

import cats.implicits._
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.{Address, EoriNumber}
import pages.traderDetails.consignment.consignee.{AddressPage, EoriNumberPage, EoriYesNoPage, NamePage}

case class ConsignmentConsigneeDomain(
  eori: Option[EoriNumber],
  name: String,
  address: Address
) extends JourneyDomainModel

object ConsignmentConsigneeDomain {

  implicit val userAnswersReader: UserAnswersReader[ConsignmentConsigneeDomain] =
    (
      EoriYesNoPage.filterOptionalDependent(identity)(EoriNumberPage.reader.map(EoriNumber(_))),
      NamePage.reader,
      AddressPage.reader
    ).tupled.map((ConsignmentConsigneeDomain.apply _).tupled)
}
