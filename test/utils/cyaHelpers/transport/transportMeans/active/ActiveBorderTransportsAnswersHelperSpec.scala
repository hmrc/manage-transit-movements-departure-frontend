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
import generators.Generators
import models.NormalMode
import models.SecurityDetailsType.{EntrySummaryDeclarationSecurityDetails, NoSecurityDetails}
import models.journeyDomain.transport.TransportMeansActiveDomain
import models.transport.transportMeans.BorderModeOfTransport
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.SecurityDetailsTypePage
import pages.transport.transportMeans.{AnotherVehicleCrossingYesNoPage, BorderModeOfTransportPage}
import viewModels.ListItem

class ActiveBorderTransportsAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "ActiveBorderTransportCheckYourAnswersHelperSpec" - {

    "when empty user answers" - {
      "must return empty list of list items" in {
        val userAnswers = emptyUserAnswers

        val helper = new ActiveBorderTransportsAnswersHelper(userAnswers, NormalMode)
        helper.listItems mustBe Nil
      }
    }

    "when user answers populated with a complete active border transport" - {
      "and AnotherVehicleCrossingBorder has been answered" - {
        "and Identification type is defined" in {
          val initialAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(AnotherVehicleCrossingYesNoPage, true)
            .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Fixed)

          forAll(arbitraryTransportMeansActiveAnswers(initialAnswers, index)) {
            userAnswers =>
              val helper = new ActiveBorderTransportsAnswersHelper(userAnswers, NormalMode)
              val active = TransportMeansActiveDomain.userAnswersReader(index).run(userAnswers).value
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = s"${messages(s"transport.transportMeans.active.identification.${active.identification}")} - ${active.identificationNumber}",
                    changeUrl = "#",
                    removeUrl =
                      Some(controllers.transport.transportMeans.active.routes.ConfirmRemoveBorderTransportController.onPageLoad(lrn, NormalMode, index).url)
                  )
                )
              )
          }
        }
      }

      "and AnotherVehicleCrossingBorder has not been answered" - {
        "and Identification type is defined" in {
          val initialAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, EntrySummaryDeclarationSecurityDetails)
            .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Fixed)

          forAll(arbitraryTransportMeansActiveAnswers(initialAnswers, index)) {
            userAnswers =>
              val helper = new ActiveBorderTransportsAnswersHelper(userAnswers, NormalMode)
              val active = TransportMeansActiveDomain.userAnswersReader(index).run(userAnswers).value
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = s"${messages(s"transport.transportMeans.active.identification.${active.identification}")} - ${active.identificationNumber}",
                    changeUrl = "#",
                    removeUrl = None
                  )
                )
              )
          }
        }
      }
    }
  }
}
