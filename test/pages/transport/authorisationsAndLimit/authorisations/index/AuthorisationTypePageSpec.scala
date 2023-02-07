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

package pages.transport.authorisationsAndLimit.authorisations.index

import models.transport.authorisations.AuthorisationType
import pages.behaviours.PageBehaviours
import org.scalacheck.Arbitrary.arbitrary

class AuthorisationTypePageSpec extends PageBehaviours {

  "AuthorisationTypePage" - {

    beRetrievable[AuthorisationType](AuthorisationTypePage(index))

    beSettable[AuthorisationType](AuthorisationTypePage(index))

    beRemovable[AuthorisationType](AuthorisationTypePage(index))

    "cleanup" - {
      val referenceNumber = arbitrary[String].sample.value

      "when answer changes" - {
        "must remove Authorisation number" in {
          forAll(arbitrary[AuthorisationType]) {
            authorisationType =>
              val userAnswers = emptyUserAnswers
                .setValue(AuthorisationTypePage(authorisationIndex), authorisationType)
                .setValue(AuthorisationReferenceNumberPage(authorisationIndex), referenceNumber)

              forAll(arbitrary[AuthorisationType].retryUntil(_ != authorisationType)) {
                differentAuthorisationType =>
                  val result = userAnswers.setValue(AuthorisationTypePage(authorisationIndex), differentAuthorisationType)

                  result.get(AuthorisationReferenceNumberPage(authorisationIndex)) must not be defined
              }
          }
        }
      }

      "when answer has not changed" - {
        "must not remove Authorisation reference number" in {
          forAll(arbitrary[AuthorisationType]) {
            authorisationType =>
              val userAnswers = emptyUserAnswers
                .setValue(AuthorisationTypePage(authorisationIndex), authorisationType)
                .setValue(AuthorisationReferenceNumberPage(authorisationIndex), referenceNumber)

              val result = userAnswers.setValue(AuthorisationTypePage(authorisationIndex), authorisationType)

              result.get(AuthorisationReferenceNumberPage(authorisationIndex)) must be(defined)

          }
        }
      }
    }
  }
}
