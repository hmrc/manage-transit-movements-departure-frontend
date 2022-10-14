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

package pages.traderDetails.consignment.consignor

import models.DynamicAddress
import models.reference.Country
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class CountryPageSpec extends PageBehaviours {

  "countryPage" - {

    beRetrievable[Country](CountryPage)

    beSettable[Country](CountryPage)

    beRemovable[Country](CountryPage)

    "cleanup" - {
      "when answer changes" - {
        "must clean up address page" in {
          def country = arbitrary[Country].sample.value

          val preChange = emptyUserAnswers
            .setValue(CountryPage, country)
            .setValue(AddressPage, Arbitrary.arbitrary[DynamicAddress].sample.value)

          val postChange = preChange.setValue(CountryPage, country)

          postChange.get(AddressPage) mustNot be(defined)
        }
      }

      "when answer does not change" - {
        "must do nothing" in {
          val country = arbitrary[Country].sample.value

          val preChange = emptyUserAnswers
            .setValue(CountryPage, country)
            .setValue(AddressPage, arbitrary[DynamicAddress].sample.value)

          val postChange = preChange.setValue(CountryPage, country)

          postChange.get(AddressPage) must be(defined)
        }
      }
    }
  }
}
