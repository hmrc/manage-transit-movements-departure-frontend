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

import config.Constants.XI
import models.ProcedureType.Normal
import pages.preTaskList.{OfficeOfDeparturePage, ProcedureTypePage}

sealed trait LocationType

object LocationType extends RadioModel[LocationType] {

  override val messageKeyPrefix = "routeDetails.locationOfGoods.locationOfGoodsType"

  case object DesignatedLocation extends WithName("DesignatedLocation") with LocationType
  case object AuthorisedPlace extends WithName("AuthorisedPlace") with LocationType
  case object ApprovedPlace extends WithName("ApprovedPlace") with LocationType
  case object Other extends WithName("Other") with LocationType

  override val values: Seq[LocationType] = Seq(
    DesignatedLocation,
    AuthorisedPlace,
    ApprovedPlace,
    Other
  )

}
