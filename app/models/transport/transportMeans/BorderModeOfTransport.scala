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

sealed trait BorderModeOfTransport {
  val borderModeType: Int
}

object BorderModeOfTransport extends RadioModel[BorderModeOfTransport] {

  case object Maritime extends WithName("maritime") with BorderModeOfTransport {
    override val borderModeType: Int = 1
  }

  case object Rail extends WithName("rail") with BorderModeOfTransport {
    override val borderModeType: Int = 2
  }

  case object Road extends WithName("road") with BorderModeOfTransport {
    override val borderModeType: Int = 3
  }

  case object Air extends WithName("air") with BorderModeOfTransport {
    override val borderModeType: Int = 4
  }

  case object Mail extends WithName("mail") with BorderModeOfTransport {
    override val borderModeType: Int = 5
  }

  case object Fixed extends WithName("fixed") with BorderModeOfTransport {
    override val borderModeType: Int = 7
  }

  case object Waterway extends WithName("waterway") with BorderModeOfTransport {
    override val borderModeType: Int = 8
  }

  override val messageKeyPrefix: String = "transport.transportMeans.borderModeOfTransport"

  val values: Seq[BorderModeOfTransport] = Seq(
    Maritime,
    Rail,
    Road,
    Air,
    Mail,
    Fixed,
    Waterway
  )
}
