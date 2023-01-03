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

package models.transport.transportMeans.active

import models.{RadioModel, WithName}
import play.api.i18n.Messages

sealed trait Identification {
  val borderModeType: Int

  def asString(implicit messages: Messages): String =
    messages(s"${Identification.messageKeyPrefix}.$this")
}

object Identification extends RadioModel[Identification] {

  case object ImoShipIdNumber extends WithName("imoShipIdNumber") with Identification {
    override val borderModeType: Int = 10
  }

  case object SeaGoingVessel extends WithName("seaGoingVessel") with Identification {
    override val borderModeType: Int = 11
  }

  case object TrainNumber extends WithName("trainNumber") with Identification {
    override val borderModeType: Int = 21
  }

  case object RegNumberRoadVehicle extends WithName("regNumberRoadVehicle") with Identification {
    override val borderModeType: Int = 30
  }

  case object IataFlightNumber extends WithName("iataFlightNumber") with Identification {
    override val borderModeType: Int = 40
  }

  case object RegNumberAircraft extends WithName("regNumberAircraft") with Identification {
    override val borderModeType: Int = 41
  }

  case object EuropeanVesselIdNumber extends WithName("europeanVesselIdNumber") with Identification {
    override val borderModeType: Int = 80
  }

  case object InlandWaterwaysVehicle extends WithName("inlandWaterwaysVehicle") with Identification {
    override val borderModeType: Int = 81
  }

  override val messageKeyPrefix: String = "transport.transportMeans.active.identification"

  val values: Seq[Identification] = Seq(
    ImoShipIdNumber,
    SeaGoingVessel,
    TrainNumber,
    RegNumberRoadVehicle,
    IataFlightNumber,
    RegNumberAircraft,
    EuropeanVesselIdNumber,
    InlandWaterwaysVehicle
  )
}
