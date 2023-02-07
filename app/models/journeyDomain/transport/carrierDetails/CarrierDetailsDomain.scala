/*
 * Copyright 2023 HM Revenue & Customs
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

package models.journeyDomain.transport.carrierDetails

import cats.implicits._
import models.domain.{UserAnswersReader, _}
import pages.transport.carrierDetails._

case class CarrierDetailsDomain(
  identificationNumber: String,
  contactPerson: Option[ContactPersonDomain]
)

object CarrierDetailsDomain {

  implicit val userAnswersReader: UserAnswersReader[CarrierDetailsDomain] =
    (
      IdentificationNumberPage.reader,
      AddContactYesNoPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((CarrierDetailsDomain.apply _).tupled)

}
