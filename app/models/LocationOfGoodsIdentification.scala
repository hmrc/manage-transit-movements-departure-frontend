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

package models

sealed trait LocationOfGoodsIdentification {
  val code: String
}

object LocationOfGoodsIdentification extends RadioModel[LocationOfGoodsIdentification] {

  case object CustomsOfficeIdentifier extends WithName("customsOfficeIdentifier") with LocationOfGoodsIdentification {
    override val code: String = "V"
  }

  case object EoriNumber extends WithName("eoriNumber") with LocationOfGoodsIdentification {
    override val code: String = "X"
  }

  case object AuthorisationNumber extends WithName("authorisationNumber") with LocationOfGoodsIdentification {
    override val code: String = "Y"
  }

  case object CoordinatesIdentifier extends WithName("coordinates") with LocationOfGoodsIdentification {
    override val code: String = "W"
  }

  case object UnlocodeIdentifier extends WithName("unlocode") with LocationOfGoodsIdentification {
    override val code: String = "U"
  }

  case object AddressIdentifier extends WithName("address") with LocationOfGoodsIdentification {
    override val code: String = "Z"
  }

  case object PostalCode extends WithName("postalCode") with LocationOfGoodsIdentification {
    override val code: String = "T"
  }

  override val messageKeyPrefix: String = "routeDetails.locationOfGoods.identification"

  val values: Seq[LocationOfGoodsIdentification] = Seq(
    CustomsOfficeIdentifier,
    EoriNumber,
    AuthorisationNumber,
    CoordinatesIdentifier,
    UnlocodeIdentifier,
    AddressIdentifier,
    PostalCode
  )
}
