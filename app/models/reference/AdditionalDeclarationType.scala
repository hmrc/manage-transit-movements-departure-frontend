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

package models.reference

import models.{DynamicEnumerableType, Radioable}
import play.api.libs.json.{Format, Json}

case class AdditionalDeclarationType(
  code: String,
  description: String
) extends Radioable[AdditionalDeclarationType] {

  override def toString: String = s"$code - $description"

  override val messageKeyPrefix: String = AdditionalDeclarationType.messageKeyPrefix
}

object AdditionalDeclarationType extends DynamicEnumerableType[AdditionalDeclarationType] {
  implicit val format: Format[AdditionalDeclarationType] = Json.format[AdditionalDeclarationType]

  val messageKeyPrefix = "additionalDeclarationType"
}
