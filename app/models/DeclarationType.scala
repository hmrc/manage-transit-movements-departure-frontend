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
import play.api.data.Form
import uk.gov.hmrc.viewmodels._

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

  def radios(form: Form[_], userAnswers: UserAnswers)(): Seq[Radios.Item] = {

    val field = form("value")
    val itemsGB = Seq(
      Radios.Radio(msg"declarationType.option1", Option1.toString),
      Radios.Radio(msg"declarationType.option2", Option2.toString)
    )
    val itemsNISimplified = Seq(
      Radios.Radio(msg"declarationType.option1", Option1.toString),
      Radios.Radio(msg"declarationType.option2", Option2.toString),
      Radios.Radio(msg"declarationType.option3", Option3.toString)
    )
    val itemsNINormal = Seq(
      Radios.Radio(msg"declarationType.option1", Option1.toString),
      Radios.Radio(msg"declarationType.option2", Option2.toString),
      Radios.Radio(msg"declarationType.option3", Option3.toString),
      Radios.Radio(msg"declarationType.option4", Option4.toString)
    )
    val countryCode = userAnswers.get(OfficeOfDeparturePage).map(_.countryId)
    val items = (countryCode, userAnswers.get(ProcedureTypePage)) match {
      case (Some(CountryCode("XI")), Some(Simplified)) => itemsNISimplified
      case (Some(CountryCode("XI")), Some(Normal))     => itemsNINormal
      case _                                           => itemsGB
    }
    Radios(field, items)
  }

  implicit val enumerable: Enumerable[DeclarationType] =
    Enumerable(
      values.map(
        v => v.toString -> v
      ): _*
    )
}
