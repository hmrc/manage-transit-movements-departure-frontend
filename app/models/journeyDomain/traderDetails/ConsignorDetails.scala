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
import models.journeyDomain.{UserAnswersReader, _}
import models.{CommonAddress, EoriNumber}
import pages.traderDetails.{ConsignorAddressPage, ConsignorEoriPage, ConsignorNamePage, IsConsignorEoriKnownPage}

case class ConsignorDetails(name: String, address: CommonAddress, eori: Option[EoriNumber])

object ConsignorDetails {

  implicit val consignorDetails: UserAnswersReader[ConsignorDetails] = {
    val readConsignorEoriPage =
      IsConsignorEoriKnownPage
        .filterOptionalDependent(identity)(ConsignorEoriPage.reader.map(EoriNumber(_)))

    (
      readConsignorEoriPage,
      ConsignorNamePage.reader,
      ConsignorAddressPage.reader
    ).tupled
      .map {
        case (eori, name, address) =>
          ConsignorDetails(name, address, eori)
      }
  }

}
