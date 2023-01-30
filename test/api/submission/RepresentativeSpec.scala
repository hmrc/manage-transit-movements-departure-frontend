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

package api.submission

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generated.{ContactPersonType05, RepresentativeType05}
import generators.Generators
import models.UserAnswers
import models.domain.UserAnswersReader
import models.journeyDomain.DepartureDomain
import models.journeyDomain.DepartureDomain.userAnswersReader

class RepresentativeSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  val uA: UserAnswers = arbitraryDepartureAnswers(emptyUserAnswers).sample.value

  "Representative" - {

    "transform is called" - {

      "will convert to API format" in {

        UserAnswersReader[DepartureDomain](userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)).run(uA).map {
          case DepartureDomain(_, traderDetails, _, _, _) =>
            val expected: Option[RepresentativeType05] = traderDetails.representative.map {
              r =>
                RepresentativeType05(
                  r.eori.value,
                  r.capacity.code,
                  Some(ContactPersonType05(r.name, r.phone, None))
                )
            }

            val converted: Option[RepresentativeType05] = Representative.transform(traderDetails)

            converted mustBe expected
        }

      }

    }

  }
}
