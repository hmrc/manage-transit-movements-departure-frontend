/*
 * Copyright 2024 HM Revenue & Customs
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

package models.reference

import cats.Order
import forms.mappings.RichSeq
import models.Selectable
import play.api.libs.json.*

case class CustomsOffice(
  id: String,
  name: String,
  phoneNumber: Option[String],
  countryId: String,
  languageCode: String
) extends Selectable {

  override def toString: String = s"$name ($id)"

  override val value: String = id
}

object CustomsOffice {
  implicit val format: OFormat[CustomsOffice] = Json.format[CustomsOffice]

  implicit val order: Order[CustomsOffice] = (x: CustomsOffice, y: CustomsOffice) => (x, y).compareBy(_.name, _.id)

  implicit val listReads: Reads[List[CustomsOffice]] =
    Reads {
      case JsArray(values) =>
        JsSuccess {
          values
            .flatMap(_.asOpt[CustomsOffice])
            .toSeq
            .groupByPreserveOrder(_.id)
            .flatMap {
              case (_, offices) =>
                offices.find(_.languageCode == "EN").orElse(offices.headOption)
            }
            .toList
        }
      case _ =>
        JsError("Expected customs offices to be in a JsArray")
    }
}
