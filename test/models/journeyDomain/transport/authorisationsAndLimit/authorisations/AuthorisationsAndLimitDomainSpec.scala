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

package models.journeyDomain.transport.authorisationsAndLimit.authorisations

import base.SpecBase
import forms.Constants.maxAuthorisationRefNumberLength
import generators.Generators
import models.SecurityDetailsType._
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.transport.authorisationsAndLimit.limit.LimitDomain
import models.transport.authorisations.AuthorisationType
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.authorisationsAndLimit.authorisations.index.{AuthorisationReferenceNumberPage, AuthorisationTypePage}

class AuthorisationsAndLimitDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "AuthorisationsAndLimitDomain" - {

    "limitReader" - {

      "can be parsed from UserAnswers" - {

        val authRefNumber = Gen.alphaNumStr.sample.value.take(maxAuthorisationRefNumberLength)

        "when AuthorisationType is ACR" in {

          val authType             = AuthorisationType.ACR
          val authorisationsDomain = AuthorisationsDomain(Seq(AuthorisationDomain(authType, authRefNumber)(authorisationIndex)))

          val userAnswers = emptyUserAnswers
            .setValue(AuthorisationTypePage(authorisationIndex), authType)
            .setValue(AuthorisationReferenceNumberPage(authorisationIndex), authRefNumber)

          forAll(arbitraryLimitAnswers(userAnswers)) {
            answers =>
              val result: EitherType[Option[LimitDomain]] = UserAnswersReader[Option[LimitDomain]](
                AuthorisationsAndLimitDomain.limitReader(authorisationsDomain)
              ).run(answers)

              result.value mustBe defined
          }
        }

        "when authorisation type is not ACR" in {

          val authType             = Gen.oneOf(AuthorisationType.TRD, AuthorisationType.SSE).sample.value
          val authorisationsDomain = AuthorisationsDomain(Seq(AuthorisationDomain(authType, authRefNumber)(authorisationIndex)))

          val result: EitherType[Option[LimitDomain]] = UserAnswersReader[Option[LimitDomain]](
            AuthorisationsAndLimitDomain.limitReader(authorisationsDomain)
          ).run(emptyUserAnswers)

          result.value must not be defined
        }

      }

    }
  }
}
