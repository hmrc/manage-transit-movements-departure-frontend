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
import models.SecurityDetailsType.{EntrySummaryDeclarationSecurityDetails, NoSecurityDetails}
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.transport.TransportMeansActiveDomain._
import models.reference.{CustomsOffice, Nationality}
import models.transport.transportMeans.active.Identification
import models.transport.transportMeans.BorderModeOfTransport.{Air, Maritime}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import pages.preTaskList.SecurityDetailsTypePage
import pages.transport.transportMeans.BorderModeOfTransportPage
import pages.transport.transportMeans.active._

class TransportMeansActiveDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "TransportMeansActiveDomain" - {

    val identification: Identification = arbitrary[Identification].sample.value
    val identificationNumber: String   = Gen.alphaNumStr.sample.value
    val nationality: Nationality       = arbitrary[Nationality].sample.value
    val customsOffice: CustomsOffice   = arbitrary[CustomsOffice].sample.value
    val conveyanceNumber: String       = Gen.alphaNumStr.sample.value

    "can be parsed from user answers" - {
      "when the add nationality is answered yes" - {
        "and security detail type is 0 and inland mode is Maritime and add conveyance number is yes" in {
          val userAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(BorderModeOfTransportPage, Maritime)
            .setValue(IdentificationPage(index), identification)
            .setValue(IdentificationNumberPage(index), identificationNumber)
            .setValue(AddNationalityYesNoPage(index), true)
            .setValue(NationalityPage(index), nationality)
            .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)
            .setValue(ConveyanceReferenceNumberYesNoPage(index), true)
            .setValue(ConveyanceReferenceNumberPage(index), conveyanceNumber)

          val expectedResult = TransportMeansActiveDomain(
            identification = identification,
            identificationNumber = identificationNumber,
            nationality = Option(nationality),
            customsOffice = customsOffice,
            conveyanceReferenceNumber = Some(conveyanceNumber)
          )

          val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](userAnswersReader(index)).run(userAnswers)

          result.value mustBe expectedResult
        }

        "and security detail type is 1 and inland mode is Air" in {
          val userAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, EntrySummaryDeclarationSecurityDetails)
            .setValue(BorderModeOfTransportPage, Air)
            .setValue(IdentificationPage(index), identification)
            .setValue(IdentificationNumberPage(index), identificationNumber)
            .setValue(AddNationalityYesNoPage(index), true)
            .setValue(NationalityPage(index), nationality)
            .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)
            .setValue(ConveyanceReferenceNumberPage(index), conveyanceNumber)

          val expectedResult = TransportMeansActiveDomain(
            identification = identification,
            identificationNumber = identificationNumber,
            nationality = Option(nationality),
            customsOffice = customsOffice,
            conveyanceReferenceNumber = Some(conveyanceNumber)
          )

          val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](userAnswersReader(index)).run(userAnswers)

          result.value mustBe expectedResult
        }
      }

      "when the add nationality is answered no" - {
        "and security detail type is 0 and inland mode is Maritime and add conveyance number is no" in {
          val userAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(BorderModeOfTransportPage, Maritime)
            .setValue(IdentificationPage(index), identification)
            .setValue(IdentificationNumberPage(index), identificationNumber)
            .setValue(AddNationalityYesNoPage(index), false)
            .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)
            .setValue(ConveyanceReferenceNumberYesNoPage(index), false)

          val expectedResult = TransportMeansActiveDomain(
            identification = identification,
            identificationNumber = identificationNumber,
            nationality = None,
            customsOffice = customsOffice,
            conveyanceReferenceNumber = None
          )

          val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](userAnswersReader(index)).run(userAnswers)

          result.value mustBe expectedResult
        }
      }

    }

    "can not be parsed from user answers" - {
      "when a mandatory page is missing and add nationality is false" in {
        val mandatoryPages: Seq[QuestionPage[_]] = Seq(
          IdentificationPage(index),
          IdentificationNumberPage(index),
          AddNationalityYesNoPage(index),
          CustomsOfficeActiveBorderPage(index),
          ConveyanceReferenceNumberYesNoPage(index),
          ConveyanceReferenceNumberPage(index)
        )

        val userAnswers = emptyUserAnswers
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(BorderModeOfTransportPage, Maritime)
          .setValue(IdentificationPage(index), identification)
          .setValue(IdentificationNumberPage(index), identificationNumber)
          .setValue(AddNationalityYesNoPage(index), false)
          .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)
          .setValue(ConveyanceReferenceNumberYesNoPage(index), true)
          .setValue(ConveyanceReferenceNumberPage(index), conveyanceNumber)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedUserAnswers = userAnswers.removeValue(mandatoryPage)

            val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](userAnswersReader(index)).run(updatedUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }

      "when a mandatory page is missing and add nationality is true" in {
        val mandatoryPages: Seq[QuestionPage[_]] = Seq(
          IdentificationPage(index),
          IdentificationNumberPage(index),
          AddNationalityYesNoPage(index),
          NationalityPage(index),
          CustomsOfficeActiveBorderPage(index),
          ConveyanceReferenceNumberYesNoPage(index),
          ConveyanceReferenceNumberPage(index)
        )

        val userAnswers = emptyUserAnswers
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(BorderModeOfTransportPage, Maritime)
          .setValue(IdentificationPage(index), identification)
          .setValue(IdentificationNumberPage(index), identificationNumber)
          .setValue(AddNationalityYesNoPage(index), true)
          .setValue(NationalityPage(index), nationality)
          .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)
          .setValue(ConveyanceReferenceNumberYesNoPage(index), true)
          .setValue(ConveyanceReferenceNumberPage(index), conveyanceNumber)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedUserAnswers = userAnswers.removeValue(mandatoryPage)

            val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](userAnswersReader(index)).run(updatedUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }
}
