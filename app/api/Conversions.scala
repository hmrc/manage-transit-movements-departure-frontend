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
import models.DateTime
import models.journeyDomain.PreTaskListDomain

import java.time.LocalDateTime

object Conversions {

  // TODO - we do not need to rely on the domain models here. We could pass in the cache directly (user answers)?
  def transitOperation(preTaskListDomain: PreTaskListDomain): TransitOperationType06 = {

    // TODO - Some test data not in the preTaskListDomain model (get from the cache down the line)
    val reducedDatasetIndicator           = false
    val bindingItinerary                  = true
    val localDateTime                     = LocalDateTime.of(2022, 1, 1, 20, 41, 0)
    val presentationOfTheGoodsDateAndTime = DateTime(localDateTime.toLocalDate, localDateTime.toLocalTime)
    val limitDate                         = DateTime(localDateTime.toLocalDate, localDateTime.toLocalTime)

    TransitOperationType06(
      preTaskListDomain.localReferenceNumber.value,
      preTaskListDomain.declarationType.toString,
      "A",
      preTaskListDomain.tirCarnetReference,
      ApiXmlHelpers.dateToXMLGregorian(presentationOfTheGoodsDateAndTime),
      preTaskListDomain.securityDetailsType.securityContentType.toString,
      ApiXmlHelpers.boolToFlag(reducedDatasetIndicator),
      None,
      None,
      ApiXmlHelpers.boolToFlag(bindingItinerary),
      ApiXmlHelpers.dateToXMLGregorian(limitDate)
    )
  }

}
