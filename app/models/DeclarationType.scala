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

import models.ProcedureType.Normal
import models.reference.CountryCode

sealed trait DeclarationType

object DeclarationType {

  case object Option1 extends WithName("T1") with DeclarationType
  case object Option2 extends WithName("T2") with DeclarationType
  case object Option3 extends WithName("T2F") with DeclarationType
  case object Option4 extends WithName("T") with DeclarationType
  case object Option5 extends WithName("TIR") with DeclarationType

  val t2Options = Seq(Option2, Option3)

  val values: Seq[DeclarationType] = Seq(
    Option1,
    Option2,
    Option3,
    Option4,
    Option5
  )

  def chooseValues(countryCode: Option[CountryCode], procedureType: Option[ProcedureType]): Seq[DeclarationType] =
    (countryCode, procedureType) match {
      case (Some(CountryCode("XI")), Some(Normal)) => Seq(Option1, Option2, Option3, Option4, Option5)
      case _                                       => Seq(Option1, Option2, Option3, Option4)
    }
}
