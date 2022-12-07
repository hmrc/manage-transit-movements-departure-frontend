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

package models.transport.transportMeans

import models.{RadioModel, WithName}

sealed trait BorderModeOfTransport

object BorderModeOfTransport extends RadioModel[BorderModeOfTransport] {

  case object Opt1 extends WithName("opt1") with BorderModeOfTransport
  case object Opt2 extends WithName("opt2") with BorderModeOfTransport

  override val messageKeyPrefix: String = "transport.transportMeans.borderModeOfTransport"

  val values: Seq[BorderModeOfTransport] = Seq(
    Opt1,
    Opt2
  )
}
