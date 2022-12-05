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

package models.transport.transportMeans.active

import models.{RadioModel, WithName}

sealed trait Identification

object Identification extends RadioModel[Identification] {

  case object ImoShipIdNumber extends WithName("imoShipIdNumber") with Identification

  case object SeaGoingVessel extends WithName("seaGoingVessel") with Identification

  case object TrainNumber extends WithName("trainNumber") with Identification

  case object RegNumberRoadVehicle extends WithName("regNumberRoadVehicle") with Identification

  case object IataFlightNumber extends WithName("iataFlightNumber") with Identification

  case object RegNumberAircraft extends WithName("regNumberAircraft") with Identification

  case object EuropeanVesselIdNumber extends WithName("europeanVesselIdNumber") with Identification

  case object InlandWaterwaysVehicle extends WithName("inlandWaterwaysVehicle") with Identification

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
