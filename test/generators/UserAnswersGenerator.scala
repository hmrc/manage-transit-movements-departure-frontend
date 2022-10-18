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

package generators

import models.domain.UserAnswersReader
import models.journeyDomain.{DepartureDomain, ReaderError}
import models.reference.Country
import models.{EoriNumber, LocalReferenceNumber, RichJsObject, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues

trait UserAnswersGenerator extends UserAnswersEntryGenerators with TryValues {
  self: Generators =>

  val ctcCountries: Seq[Country]                            = listWithMaxLength[Country]().sample.get
  val ctcCountryCodes: Seq[String]                          = ctcCountries.map(_.code.code)
  val customsSecurityAgreementAreaCountries: Seq[Country]   = listWithMaxLength[Country]().sample.get
  val customsSecurityAgreementAreaCountryCodes: Seq[String] = customsSecurityAgreementAreaCountries.map(_.code.code)

  def arbitraryDepartureAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[DepartureDomain](userAnswers)(
      DepartureDomain.userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
    )

  implicit lazy val arbitraryUserAnswers: Arbitrary[UserAnswers] =
    Arbitrary {
      for {
        lrn        <- arbitrary[LocalReferenceNumber]
        eoriNumber <- arbitrary[EoriNumber]
        initialAnswers = UserAnswers(lrn, eoriNumber)
        answers <- arbitraryDepartureAnswers(initialAnswers)
      } yield answers
    }

  protected def buildUserAnswers[T](
    initialUserAnswers: UserAnswers
  )(implicit userAnswersReader: UserAnswersReader[T]): Gen[UserAnswers] = {

    def rec(userAnswers: UserAnswers): Gen[UserAnswers] =
      UserAnswersReader[T].run(userAnswers) match {
        case Left(ReaderError(page, _)) =>
          generateAnswer
            .apply(page)
            .map {
              value =>
                userAnswers.copy(
                  data = userAnswers.data.setObject(page.path, value).getOrElse(userAnswers.data)
                )
            }
            .flatMap(rec)
        case Right(_) => Gen.const(userAnswers)
      }

    rec(initialUserAnswers)
  }
}
