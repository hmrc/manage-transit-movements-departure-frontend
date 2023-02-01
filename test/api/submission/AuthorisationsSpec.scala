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
import generated._
import generators.Generators
import models.domain.UserAnswersReader
import models.journeyDomain.DepartureDomain
import models.journeyDomain.DepartureDomain.userAnswersReader
import models.journeyDomain.transport.authorisationsAndLimit.authorisations.AuthorisationsDomain

class AuthorisationsSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "Authorisations" - {

    "transform is called" - {

      "will convert to API format" in {

        arbitraryDepartureAnswers(emptyUserAnswers).map(
          arbitraryDepartureUserAnswers =>
            UserAnswersReader[DepartureDomain](userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes))
              .run(arbitraryDepartureUserAnswers)
              .map {
                case DepartureDomain(_, _, _, _, transportDetails) =>
                  val domain: Option[AuthorisationsDomain] = transportDetails.authorisationsAndLimit.map(
                    x => x.authorisationsDomain
                  )

                  val expected: Seq[AuthorisationType03] = domain
                    .map(
                      authorisation =>
                        authorisation.authorisations.map(
                          a =>
                            AuthorisationType03(
                              authorisation.authorisations.indexOf(a).toString,
                              a.authorisationType.toString,
                              a.referenceNumber
                            )
                        )
                    )
                    .getOrElse(Seq.empty)

                  val converted: Seq[AuthorisationType03] = Authorisations.transform(
                    transportDetails.authorisationsAndLimit.map(
                      x => x.authorisationsDomain
                    )
                  )

                  converted mustBe expected
              }
        )

      }

    }

  }
}