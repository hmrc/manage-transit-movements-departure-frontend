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

package models.transportDetails.transportMeans.departure

import models.{RadioModel, WithName}

sealed trait InlandMode

object InlandMode extends RadioModel[InlandMode] {

  case object Maritime extends WithName("maritime") with InlandMode
  case object Rail extends WithName("rail") with InlandMode
  case object Road extends WithName("road") with InlandMode
  case object Air extends WithName("air") with InlandMode
  case object Mail extends WithName("mail") with InlandMode
  case object Fixed extends WithName("fixed") with InlandMode
  case object Waterway extends WithName("waterway") with InlandMode
  case object Unknown extends WithName("unknown") with InlandMode

  override val messageKeyPrefix: String = "transportDetails.transportMeans.departure.inlandMode"

  val values: Seq[InlandMode] = Seq(
    Maritime,
    Rail,
    Road,
    Air,
    Mail,
    Fixed,
    Waterway,
    Unknown
  )
}
