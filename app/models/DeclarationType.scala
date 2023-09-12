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

import config.Constants.{TIR, XI}
import models.ProcedureType.Normal
import pages.preTaskList.{OfficeOfDeparturePage, ProcedureTypePage}
import play.api.libs.json.{Format, Json}

case class DeclarationType(
  code: String,
  description: String
) extends Radioable[DeclarationType] {
  override def toString: String         = s"$code - $description"
  override val messageKeyPrefix: String = DeclarationType.messageKeyPrefix
  def isTIR: Boolean                    = code == TIR
}

object DeclarationType extends DynamicEnumerableType[DeclarationType] {

  implicit val format: Format[DeclarationType] = Json.format[DeclarationType]

  val messageKeyPrefix: String = "declarationType"

  def values(userAnswers: UserAnswers, declarationTypes: Seq[DeclarationType]) =
    (
      userAnswers.get(OfficeOfDeparturePage).map(_.countryCode),
      userAnswers.get(ProcedureTypePage)
    ) match {
      case (Some(XI), Some(Normal)) => declarationTypes
      case _                        => declarationTypes.filterNot(_.code == TIR)
    }
}
