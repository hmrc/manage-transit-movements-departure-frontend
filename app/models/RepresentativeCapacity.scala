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

import play.api.data.Form
import uk.gov.hmrc.viewmodels._

sealed trait RepresentativeCapacity

object RepresentativeCapacity extends Enumerable.Implicits {

  case object Direct extends WithName("direct") with RepresentativeCapacity
  case object Indirect extends WithName("indirect") with RepresentativeCapacity

  val values: Seq[RepresentativeCapacity] = Seq(
    Direct,
    Indirect
  )

  def radios(form: Form[_]): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(msg"representativeCapacity.direct", Direct.toString),
      Radios.Radio(msg"representativeCapacity.indirect", Indirect.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[RepresentativeCapacity] =
    Enumerable(
      values.map(
        v => v.toString -> v
      ): _*
    )
}
