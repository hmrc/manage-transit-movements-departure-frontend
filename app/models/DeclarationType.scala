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

import config.Constants.XI
import models.ProcedureType.Normal
import pages.preTaskList.{OfficeOfDeparturePage, ProcedureTypePage}

sealed trait DeclarationType

object DeclarationType extends RadioModelU[DeclarationType] {

  override val messageKeyPrefix = "declarationType"

  case object Option1 extends WithName("T1") with DeclarationType
  case object Option2 extends WithName("T2") with DeclarationType
  case object Option3 extends WithName("T2F") with DeclarationType
  case object Option4 extends WithName("TIR") with DeclarationType
  case object Option5 extends WithName("T") with DeclarationType

  val t2Options = Seq(Option2, Option3)

  override val values: Seq[DeclarationType] = Seq(
    Option1,
    Option2,
    Option3,
    Option4,
    Option5
  )

  override def valuesU(userAnswers: UserAnswers): Seq[DeclarationType] =
    (
      userAnswers.get(OfficeOfDeparturePage).map(_.countryCode),
      userAnswers.get(ProcedureTypePage)
    ) match {
      case (Some(XI), Some(Normal)) => values
      case _                        => values.filterNot(_ == Option4)
    }
}
