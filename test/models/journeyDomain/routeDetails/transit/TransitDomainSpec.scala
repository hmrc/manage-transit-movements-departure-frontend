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

package models.journeyDomain.routeDetails.transit

import java.time.LocalDateTime

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.transit._

class TransitDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "TransitDomain" - {
    val t2DeclarationType          = true
    val addOfficeOfTransitYesNo    = true
    val customsOffice              = arbitrary[CustomsOffice].sample.value
    val country                    = arbitrary[Country].sample.value
    val addOfficeOfTransitETAYesNo = true
    val officeOfTransitETA         = LocalDateTime.now

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are answered" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(T2DeclarationTypeYesNoPage)(t2DeclarationType)
          .unsafeSetVal(AddOfficeOfTransitYesNoPage)(addOfficeOfTransitYesNo)
          .unsafeSetVal(OfficeOfTransitCountryPage(index))(country)
          .unsafeSetVal(OfficeOfTransitPage(index))(customsOffice)
          .unsafeSetVal(AddOfficeOfTransitETAYesNoPage(index))(addOfficeOfTransitETAYesNo)
          .unsafeSetVal(OfficeOfTransitETAPage(index))(officeOfTransitETA)

        val expectedResult = TransitDomain(
          t2DeclarationType = t2DeclarationType,
          addOfficeOfTransit = addOfficeOfTransitYesNo,
          officesOfTransitCountries = Seq(
            OfficeOfTransitCountryDomain(country, customsOffice, addOfficeOfTransitETAYesNo, officeOfTransitETA)(index)
          )
        )

        val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }
  }
}
