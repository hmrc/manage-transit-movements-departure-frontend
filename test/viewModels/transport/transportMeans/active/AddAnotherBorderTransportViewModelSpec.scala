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

package viewModels.transport.transportMeans.active

import base.SpecBase
import generators.Generators
import models.Index
import models.SecurityDetailsType.NoSecurityDetails
import models.reference.CustomsOffice
import models.transport.transportMeans.active.Identification
import org.scalacheck.Arbitrary.arbitrary
import pages.preTaskList.SecurityDetailsTypePage
import pages.transport.transportMeans.active._

class AddAnotherBorderTransportViewModelSpec extends SpecBase with Generators {

  "must get list items" in {
    val identificationType = arbitrary[Identification].retryUntil(_ != Identification.IataFlightNumber).sample.value
    val userAnswers = emptyUserAnswers
      .setValue(SecurityDetailsTypePage, NoSecurityDetails)
      .setValue(IdentificationPage(Index(0)), identificationType)
      .setValue(IdentificationNumberPage(Index(0)), "1234")
      .setValue(AddNationalityYesNoPage(Index(0)), false)
      .setValue(CustomsOfficeActiveBorderPage(Index(0)), arbitrary[CustomsOffice].sample.value)
      .setValue(ConveyanceReferenceNumberPage(Index(0)), "1234")
      .setValue(IdentificationPage(Index(1)), identificationType)

    val result = AddAnotherBorderTransportViewModel(userAnswers)
    result.listItems.length mustBe 2
  }
}
