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
import config.FrontendAppConfig
import models.{DynamicEnumerableType, Radioable}
import org.apache.commons.text.StringEscapeUtils
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{__, Format, Json, Reads}

case class SecurityType(
  code: String,
  description: String
) extends Radioable[SecurityType] {

  override def toString: String = StringEscapeUtils.unescapeXml(description)

  override val messageKeyPrefix: String = "securityDetailsType"
}

object SecurityType extends DynamicEnumerableType[SecurityType] {

  def reads(config: FrontendAppConfig): Reads[SecurityType] =
    if (config.phase6Enabled) {
      (
        (__ \ "key").read[String] and
          (__ \ "value").read[String]
      )(SecurityType.apply)
    } else {
      Json.reads[SecurityType]
    }

  implicit val format: Format[SecurityType] = Json.format[SecurityType]

  implicit val order: Order[SecurityType] = (x: SecurityType, y: SecurityType) => (x, y).compareBy(_.code)
}
