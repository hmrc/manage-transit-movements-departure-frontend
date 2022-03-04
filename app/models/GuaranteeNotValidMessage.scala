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

package models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{__, XmlReader}
import play.api.libs.json.{Json, OWrites}

case class GuaranteeNotValidMessage(mrn: String, reasonCodes: Seq[InvalidGuaranteeReasonCode])

object GuaranteeNotValidMessage {

  implicit val writes: OWrites[GuaranteeNotValidMessage] = Json.writes[GuaranteeNotValidMessage]

  implicit val xmlReader: XmlReader[GuaranteeNotValidMessage] = (
    (__ \ "HEAHEA" \ "DocNumHEA5").read[String],
    (__ \ "GUAREF2").read(strictReadSeq[InvalidGuaranteeReasonCode])
  ).mapN(apply)
}
