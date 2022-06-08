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

package models.traderDetails.representative

import models.{RadioModel, WithName}

sealed trait RepresentativeCapacity

object RepresentativeCapacity extends RadioModel[RepresentativeCapacity] {

  case object Direct extends WithName("direct") with RepresentativeCapacity
  case object Indirect extends WithName("indirect") with RepresentativeCapacity

  override val messageKeyPrefix: String = "traderDetails.representative.capacity"

  val values: Seq[RepresentativeCapacity] = Seq(
    Direct,
    Indirect
  )
}
