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

import models.ProcedureType.{Normal, Simplified}
import models.reference.CountryCode
import pages.{OfficeOfDeparturePage, ProcedureTypePage}

sealed abstract class DeclarationType(val code: String, asString: String) extends WithName(asString)

object DeclarationType extends Enumerable.Implicits {

  case object Option1 extends DeclarationType("T1", "option1")
  case object Option2 extends DeclarationType("T2", "option2")
  case object Option3 extends DeclarationType("T2F", "option3")
  case object Option4 extends DeclarationType("TIR", "option4")

  val t2Options = Seq(Option2, Option3)

  val values: Seq[DeclarationType] = Seq(
    Option1,
    Option2,
    Option3,
    Option4
  )

  def chooseValues(countryCode: Option[CountryCode], procedureType: Option[ProcedureType]): Seq[DeclarationType] =
    (countryCode, procedureType) match {
      case (Some(CountryCode("XI")), Some(Simplified)) => Seq(Option1, Option2, Option3)
      case (Some(CountryCode("XI")), Some(Normal))     => Seq(Option1, Option2, Option3, Option4)
      case _                                           => Seq(Option1, Option2)
    }

  implicit val enumerable: Enumerable[DeclarationType] =
    Enumerable(
      values.map(
        v => v.toString -> v
      ): _*
    )
}

case class DeclarationTypeViewModel(userAnswers: UserAnswers) extends RadioModel[DeclarationType] {

  override val messageKeyPrefix: String = "declarationType"

  override val values: Seq[DeclarationType] = {
    val countryCode   = userAnswers.get(OfficeOfDeparturePage).map(_.countryId)
    val procedureType = userAnswers.get(ProcedureTypePage)
    DeclarationType.chooseValues(countryCode, procedureType)
  }

}
