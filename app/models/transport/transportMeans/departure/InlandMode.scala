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

package models.transport.transportMeans.departure

import models.{RadioModel, WithName}

sealed trait InlandMode {
  val inlandModeType: Int
}

object InlandMode extends RadioModel[InlandMode] {

  case object Maritime extends WithName("maritime") with InlandMode {
    override val inlandModeType: Int = 1
  }

  case object Rail extends WithName("rail") with InlandMode {
    override val inlandModeType: Int = 2
  }

  case object Road extends WithName("road") with InlandMode {
    override val inlandModeType: Int = 3
  }

  case object Air extends WithName("air") with InlandMode {
    override val inlandModeType: Int = 4
  }

  case object Mail extends WithName("mail") with InlandMode {
    override val inlandModeType: Int = 5
  }

  case object Fixed extends WithName("fixed") with InlandMode {
    override val inlandModeType: Int = 7
  }

  case object Waterway extends WithName("waterway") with InlandMode {
    override val inlandModeType: Int = 8
  }

  case object Unknown extends WithName("unknown") with InlandMode {
    override val inlandModeType: Int = 9
  }

  override val messageKeyPrefix: String = "transport.transportMeans.departure.inlandMode"

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
