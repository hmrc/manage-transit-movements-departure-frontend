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

package models.journeyDomain.transport

import base.SpecBase
import generators.Generators
import models.DeclarationType
import models.DeclarationType.Option4
import models.domain.{EitherType, UserAnswersReader}
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.QuestionPage
import pages.preTaskList.DeclarationTypePage
import pages.transport.preRequisites._

class PreRequisitesDomainSpec extends SpecBase with Generators {

  private val ucr                     = Gen.alphaNumStr.sample.value
  private val country                 = arbitrary[Country].sample.value
  private val itemsDestinationCountry = arbitrary[Country].sample.value

  "PreRequisitesDomain" - {

    "can be parsed from user answers" - {
      "when using same UCR for all items " in {
        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value)
          .setValue(SameUcrYesNoPage, true)
          .setValue(UniqueConsignmentReferencePage, ucr)
          .setValue(TransportedToSameCountryYesNoPage, true)
          .setValue(ItemsDestinationCountryPage, itemsDestinationCountry)

        val expectedResult = PreRequisitesDomain(
          ucr = Some(ucr),
          countryOfDispatch = None,
          itemsDestinationCountry = Some(itemsDestinationCountry)
        )

        val result: EitherType[PreRequisitesDomain] = UserAnswersReader[PreRequisitesDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when TIR declaration type" in {
        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, Option4)
          .setValue(SameUcrYesNoPage, false)
          .setValue(CountryOfDispatchPage, country)
          .setValue(TransportedToSameCountryYesNoPage, true)
          .setValue(ItemsDestinationCountryPage, itemsDestinationCountry)

        val expectedResult = PreRequisitesDomain(
          ucr = None,
          countryOfDispatch = Some(country),
          itemsDestinationCountry = Some(itemsDestinationCountry)
        )

        val result: EitherType[PreRequisitesDomain] = UserAnswersReader[PreRequisitesDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "can not be parsed from user answers" - {
      "when answers are empty" in {
        val result: EitherType[PreRequisitesDomain] = UserAnswersReader[PreRequisitesDomain].run(emptyUserAnswers)

        result.left.value.page mustBe SameUcrYesNoPage
      }

      "when mandatory page is missing" - {
        "when TIR" in {
          val mandatoryPages: Seq[QuestionPage[_]] = Seq(
            SameUcrYesNoPage,
            UniqueConsignmentReferencePage,
            CountryOfDispatchPage,
            TransportedToSameCountryYesNoPage,
            ItemsDestinationCountryPage
          )

          val userAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, Option4)
            .setValue(SameUcrYesNoPage, true)
            .setValue(UniqueConsignmentReferencePage, ucr)
            .setValue(CountryOfDispatchPage, country)
            .setValue(TransportedToSameCountryYesNoPage, true)
            .setValue(ItemsDestinationCountryPage, itemsDestinationCountry)

          mandatoryPages.map {
            mandatoryPage =>
              val updatedAnswers = userAnswers.removeValue(mandatoryPage)

              val result: EitherType[PreRequisitesDomain] = UserAnswersReader[PreRequisitesDomain].run(updatedAnswers)

              result.left.value.page mustBe mandatoryPage
          }
        }

        "when non-TIR" in {
          val mandatoryPages: Seq[QuestionPage[_]] = Seq(
            SameUcrYesNoPage,
            UniqueConsignmentReferencePage,
            TransportedToSameCountryYesNoPage,
            ItemsDestinationCountryPage
          )

          val userAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value)
            .setValue(SameUcrYesNoPage, true)
            .setValue(UniqueConsignmentReferencePage, ucr)
            .setValue(TransportedToSameCountryYesNoPage, true)
            .setValue(ItemsDestinationCountryPage, itemsDestinationCountry)

          mandatoryPages.map {
            mandatoryPage =>
              val updatedAnswers = userAnswers.removeValue(mandatoryPage)

              val result: EitherType[PreRequisitesDomain] = UserAnswersReader[PreRequisitesDomain].run(updatedAnswers)

              result.left.value.page mustBe mandatoryPage
          }
        }
      }
    }
  }
}
