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

package utils.cyaHelpers.transport.transportMeans.active

import base.SpecBase
import controllers.transport.transportMeans.active.routes
import generators.Generators
import models.SecurityDetailsType.{EntrySummaryDeclarationSecurityDetails, NoSecurityDetails}
import models.journeyDomain.transport.TransportMeansActiveDomain
import models.transport.transportMeans.BorderModeOfTransport
import models.transport.transportMeans.active.Identification
import models.{Mode, NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.SecurityDetailsTypePage
import pages.sections.routeDetails.transit.OfficesOfTransitSection
import pages.transport.transportMeans.active._
import pages.transport.transportMeans.{AnotherVehicleCrossingYesNoPage, BorderModeOfTransportPage}
import play.api.libs.json.JsArray
import viewModels.ListItem

class ActiveBorderTransportsAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val prefix = "transport.transportMeans.active.identification"

  private def officesOfTransit(userAnswers: UserAnswers): JsArray =
    arbitraryOfficeOfTransitAnswers(userAnswers, index).sample.value.getValue(OfficesOfTransitSection)

  "ActiveBorderTransportCheckYourAnswersHelperSpec" - {

    "when empty user answers" - {
      "must return empty list of list items" in {
        val userAnswers = emptyUserAnswers

        val helper = new ActiveBorderTransportsAnswersHelper(userAnswers, NormalMode)
        helper.listItems mustBe Nil
      }
    }

    "when user answers populated with a complete active border transport" - {
      "and AnotherVehicleCrossingBorder has been answered" in {
        val initialAnswers = emptyUserAnswers
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(OfficesOfTransitSection, officesOfTransit _)
          .setValue(AnotherVehicleCrossingYesNoPage, true)
          .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Fixed)

        forAll(arbitraryTransportMeansActiveAnswers(initialAnswers, index), arbitrary[Mode]) {
          (userAnswers, mode) =>
            val helper = new ActiveBorderTransportsAnswersHelper(userAnswers, mode)
            val active = TransportMeansActiveDomain.userAnswersReader(index).run(userAnswers).value
            helper.listItems mustBe Seq(
              Right(
                ListItem(
                  name = s"${messages(s"$prefix.${active.identification}")} - ${active.identificationNumber}",
                  changeUrl = routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn, mode, activeIndex).url,
                  removeUrl = Some(routes.ConfirmRemoveBorderTransportController.onPageLoad(userAnswers.lrn, mode, index).url)
                )
              )
            )
        }
      }

      "and AnotherVehicleCrossingBorder has not been answered" in {
        val initialAnswers = emptyUserAnswers
          .setValue(SecurityDetailsTypePage, EntrySummaryDeclarationSecurityDetails)
          .setValue(OfficesOfTransitSection, officesOfTransit _)
          .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Fixed)

        forAll(arbitraryTransportMeansActiveAnswers(initialAnswers, index), arbitrary[Mode]) {
          (userAnswers, mode) =>
            val helper = new ActiveBorderTransportsAnswersHelper(userAnswers, mode)
            val active = TransportMeansActiveDomain.userAnswersReader(index).run(userAnswers).value
            helper.listItems mustBe Seq(
              Right(
                ListItem(
                  name = s"${messages(s"$prefix.${active.identification}")} - ${active.identificationNumber}",
                  changeUrl = routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn, mode, activeIndex).url,
                  removeUrl = None
                )
              )
            )
        }
      }
    }

    "when user answers populated with an in progress active border transport" - {
      "and identification type is defined" in {
        val identificationType = Identification.SeaGoingVessel
        val userAnswers = emptyUserAnswers
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(AnotherVehicleCrossingYesNoPage, true)
          .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Fixed)
          .setValue(IdentificationPage(index), identificationType)

        forAll(arbitrary[Mode]) {
          mode =>
            val helper = new ActiveBorderTransportsAnswersHelper(userAnswers, mode)
            helper.listItems mustBe Seq(
              Left(
                ListItem(
                  name = s"${messages(s"$prefix.$identificationType")}",
                  changeUrl = routes.IdentificationNumberController.onPageLoad(userAnswers.lrn, mode, index).url,
                  removeUrl = Some(routes.ConfirmRemoveBorderTransportController.onPageLoad(userAnswers.lrn, mode, index).url)
                )
              )
            )
        }
      }

      "and identification number is defined" in {
        val identificationNumber = nonEmptyString.sample.value
        val userAnswers = emptyUserAnswers
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(AnotherVehicleCrossingYesNoPage, true)
          .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Rail)
          .setValue(IdentificationNumberPage(index), identificationNumber)

        forAll(arbitrary[Mode]) {
          mode =>
            val helper = new ActiveBorderTransportsAnswersHelper(userAnswers, mode)
            helper.listItems mustBe Seq(
              Left(
                ListItem(
                  name = identificationNumber,
                  changeUrl = routes.AddNationalityYesNoController.onPageLoad(userAnswers.lrn, mode, index).url,
                  removeUrl = Some(routes.ConfirmRemoveBorderTransportController.onPageLoad(userAnswers.lrn, mode, index).url)
                )
              )
            )
        }
      }

      "and identification type and identification number is defined" in {
        val identificationType   = Identification.SeaGoingVessel
        val identificationNumber = nonEmptyString.sample.value
        val userAnswers = emptyUserAnswers
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(AnotherVehicleCrossingYesNoPage, true)
          .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Fixed)
          .setValue(IdentificationPage(index), identificationType)
          .setValue(IdentificationNumberPage(index), identificationNumber)

        forAll(arbitrary[Mode]) {
          mode =>
            val helper = new ActiveBorderTransportsAnswersHelper(userAnswers, mode)
            helper.listItems mustBe Seq(
              Left(
                ListItem(
                  name = s"${messages(s"$prefix.$identificationType")} - $identificationNumber",
                  changeUrl = routes.AddNationalityYesNoController.onPageLoad(userAnswers.lrn, mode, index).url,
                  removeUrl = Some(routes.ConfirmRemoveBorderTransportController.onPageLoad(userAnswers.lrn, mode, index).url)
                )
              )
            )
        }
      }
    }
  }
}
