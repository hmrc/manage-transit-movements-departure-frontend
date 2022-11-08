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

package api

import generated._
import models.journeyDomain.PreTaskListDomain

object Conversions {

  def cc004CType(preTaskListDomain: PreTaskListDomain): CC004CType = {
    // map these elements from preTaskListDomain
    val m1: MESSAGESequence                       = ???
    val to: TransitOperationType01                = ???
    val cod: CustomsOfficeOfDepartureType03       = ???
    val holder: HolderOfTheTransitProcedureType20 = ???

    CC004CType(m1, to, cod, holder)
  }

}
