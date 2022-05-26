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

package models.journeyDomain.traderDetails

import cats.implicits._
import models.domain._
import models.{Address, EoriNumber}
import pages.traderDetails.holderOfTransit._

case class HolderOfTransitDomain(
  tir: Option[String],
  eori: Option[EoriNumber],
  name: String,
  address: Address,
  contactName: Option[String]
)

object HolderOfTransitDomain {

  private val tir: UserAnswersReader[Option[String]] =
    TirIdentificationYesNoPage
      .filterOptionalDependent(identity)(TirIdentificationPage.reader)

  private val eori: UserAnswersReader[Option[EoriNumber]] =
    EoriYesNoPage
      .filterOptionalDependent(identity)(EoriPage.reader.map(EoriNumber(_)))

  private val contactName: UserAnswersReader[Option[String]] =
    AddContactPage
      .filterOptionalDependent(identity)(ContactNamePage.reader)

  implicit val userAnswersReader: UserAnswersReader[HolderOfTransitDomain] =
    (
      tir,
      eori,
      NamePage.reader,
      AddressPage.reader,
      contactName
    ).tupled.map((HolderOfTransitDomain.apply _).tupled)
}
