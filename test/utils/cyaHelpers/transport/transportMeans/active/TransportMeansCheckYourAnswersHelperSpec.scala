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
import models.Mode
import models.reference.{CustomsOffice, Nationality}
import models.transport.transportMeans.departure.{InlandMode, Identification => DepartureIdentification}
import models.transport.transportMeans.active.{Identification => ActiveIdentification}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.transportMeans.active.{
  AddNationalityYesNoPage,
  ConveyanceReferenceNumberPage,
  ConveyanceReferenceNumberYesNoPage,
  CustomsOfficeActiveBorderPage,
  IdentificationNumberPage,
  IdentificationPage,
  NationalityPage
}
import pages.transport.transportMeans.departure.{
  InlandModePage,
  MeansIdentificationNumberPage,
  VehicleCountryPage,
  IdentificationPage => DepartureIdentificationPage
}
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions}

class TransportMeansCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "TransportMeansCheckYourAnswersHelper" - {

    "inlandMode" - {
      "must return None" - {
        "when inlandModePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportMeansCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.inlandMode
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when inlandModePage defined" in {
          forAll(arbitrary[Mode], arbitrary[InlandMode]) {
            (mode, inlandMode) =>
              val answers = emptyUserAnswers.setValue(InlandModePage, inlandMode)
              val helper  = new TransportMeansCheckYourAnswersHelper(answers, mode)
              val result  = helper.inlandMode

              result mustBe Some(
                SummaryListRow(
                  key = Key("Which inland mode of transport are you using?".toText),
                  value = Value(s"$inlandMode".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.transport.transportMeans.departure.routes.InlandModeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("which inland mode of transport you’re using"),
                          attributes = Map("id" -> "change-transport-means-inland-mode")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "departureIdentificationType" - {
      "must return None" - {
        "when departureIdentificationPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportMeansCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.departureIdentificationType
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when departureIdentificationPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[DepartureIdentification]) {
            (mode, departureIdentification) =>
              val answers = emptyUserAnswers.setValue(DepartureIdentificationPage, departureIdentification)
              val helper  = new TransportMeansCheckYourAnswersHelper(answers, mode)
              val result  = helper.departureIdentificationType

              result mustBe Some(
                SummaryListRow(
                  key = Key("Identification type".toText),
                  value = Value(s"$departureIdentification".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.transport.transportMeans.departure.routes.IdentificationController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("identification type for the departure means of transport"),
                          attributes = Map("id" -> "change-transport-means-identification")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "departureIdentificationNumber" - {
      "must return None" - {
        "when departureIdentificationNumberPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportMeansCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.departureIdentificationNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when departureIdentificationNumberPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, identificationNumber) =>
              val answers = emptyUserAnswers.setValue(MeansIdentificationNumberPage, identificationNumber)
              val helper  = new TransportMeansCheckYourAnswersHelper(answers, mode)
              val result  = helper.departureIdentificationNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("Identification number".toText),
                  value = Value(s"$identificationNumber".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.transport.transportMeans.departure.routes.MeansIdentificationNumberController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("identification number for the departure means of transport"),
                          attributes = Map("id" -> "change-transport-means-identification-number")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "departureNationality" - {
      "must return None" - {
        "when vehicleCountryPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportMeansCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.departureNationality
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when vehicleCountryPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[Nationality]) {
            (mode, nationality) =>
              val answers = emptyUserAnswers.setValue(VehicleCountryPage, nationality)
              val helper  = new TransportMeansCheckYourAnswersHelper(answers, mode)
              val result  = helper.departureNationality

              result mustBe Some(
                SummaryListRow(
                  key = Key("Registered country".toText),
                  value = Value(s"$nationality".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.transport.transportMeans.departure.routes.VehicleCountryController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("registered country for the departure means of transport"),
                          attributes = Map("id" -> "change-transport-means-vehicle-nationality")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "activeBorderIdentificationType" - {
      "must return None" - {
        "when ActiveBorderIdentificationTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportMeansCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.activeBorderIdentificationType(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ActiveBorderIdentificationTypePage is defined" in {
          forAll(arbitrary[Mode], arbitrary[ActiveIdentification]) {
            (mode, activeIdentification) =>
              val answers = emptyUserAnswers.setValue(IdentificationPage(index), activeIdentification)
              val helper  = new TransportMeansCheckYourAnswersHelper(answers, mode)
              val result  = helper.activeBorderIdentificationType(index)

              result mustBe Some(
                SummaryListRow(
                  key = Key("Identification type".toText),
                  value = Value(messages(s"${"transport.transportMeans.active.identification"}.$activeIdentification").toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.transport.transportMeans.active.routes.IdentificationController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("identification type for the border means of transport"),
                          attributes = Map("id" -> "change-transport-means-identification")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "activeBorderIdentificationNumber" - {
      "must return None" - {
        "when ActiveBorderIdentificationNumberPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportMeansCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.activeBorderIdentificationNumber(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ActiveBorderIdentificationNumberPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, identificationNumber) =>
              val answers = emptyUserAnswers.setValue(IdentificationNumberPage(index), identificationNumber)
              val helper  = new TransportMeansCheckYourAnswersHelper(answers, mode)
              val result  = helper.activeBorderIdentificationNumber(index)

              result mustBe Some(
                SummaryListRow(
                  key = Key("Identification number".toText),
                  value = Value(messages(s"${"transport.transportMeans.active.identification"}.$identificationNumber").toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.transport.transportMeans.active.routes.IdentificationNumberController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("identification number for the border means of transport"),
                          attributes = Map("id" -> "change-transport-means-identification-number")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "activeBorderAddNationality" - {
      "must return None" - {
        "when addNationality is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportMeansCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.activeBorderAddNationality(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when addNationality is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddNationalityYesNoPage(index), true)
              val helper  = new TransportMeansCheckYourAnswersHelper(answers, mode)
              val result  = helper.activeBorderAddNationality(index)

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add a registered country?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.transport.transportMeans.active.routes.AddNationalityYesNoController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("if you want to add a registered country"),
                          attributes = Map("id" -> "change-add-transport-means-vehicle-nationality")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }

    }

    "activeBorderNationality" - {
      "must return None" - {
        "when NationalityPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportMeansCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.activeBorderNationality(index)
              result mustBe None
          }
        }

        "must return Some(Row)" - {
          "when vehicleCountryPage is defined" in {
            forAll(arbitrary[Mode], arbitrary[Nationality]) {
              (mode, nationality) =>
                val answers = emptyUserAnswers.setValue(NationalityPage(index), nationality)
                val helper  = new TransportMeansCheckYourAnswersHelper(answers, mode)
                val result  = helper.activeBorderNationality(index)

                result mustBe Some(
                  SummaryListRow(
                    key = Key("Registered country".toText),
                    value = Value(messages(s"${"transport.transportMeans.active.identification"}.$nationality").toText),
                    actions = Some(
                      Actions(
                        items = List(
                          ActionItem(
                            content = "Change".toText,
                            href = controllers.transport.transportMeans.active.routes.NationalityController.onPageLoad(answers.lrn, mode, index).url,
                            visuallyHiddenText = Some("registered country for the border means of transport"),
                            attributes = Map("id" -> "change-transport-means-vehicle-nationality")
                          )
                        )
                      )
                    )
                  )
                )
            }
          }
        }
      }
    }

    "customsOfficeAtBorder" - {
      "must return None" - {
        "when CustomsOfficeActiveBorderPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportMeansCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.customsOfficeAtBorder(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when CustomsOfficeActiveBorderPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[CustomsOffice]) {
            (mode, customsOffice) =>
              val answers = emptyUserAnswers.setValue(CustomsOfficeActiveBorderPage(index), customsOffice)
              val helper  = new TransportMeansCheckYourAnswersHelper(answers, mode)
              val result  = helper.customsOfficeAtBorder(index)

              result mustBe Some(
                SummaryListRow(
                  key = Key("Office of transit".toText),
                  value = Value(s"$customsOffice".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href =
                            controllers.transport.transportMeans.active.routes.CustomsOfficeActiveBorderController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("office of transit for the border means of transport"),
                          attributes = Map("id" -> "change-transport-means-customs-office-at-border")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "activeBorderConveyanceReferenceNumberYesNo" - {
      "must return None" - {
        "when ActiveBorderConveyanceReferenceNumberYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportMeansCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.activeBorderConveyanceReferenceNumberYesNo(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when activeBorderConveyanceReferenceNumberYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(ConveyanceReferenceNumberYesNoPage(index), true)
              val helper  = new TransportMeansCheckYourAnswersHelper(answers, mode)
              val result  = helper.activeBorderConveyanceReferenceNumberYesNo(index)

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add a conveyance reference number?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.transport.transportMeans.active.routes.ConveyanceReferenceNumberYesNoController
                            .onPageLoad(answers.lrn, mode, index)
                            .url,
                          visuallyHiddenText = Some("if you want to add a conveyance reference number"),
                          attributes = Map("id" -> "change-add-transport-means-conveyance-reference-number")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }

    }

    "conveyanceReferenceNumber" - {
      "must return None" - {
        "when ConveyanceReferenceNumberPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransportMeansCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.conveyanceReferenceNumber(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ConveyanceReferenceNumberPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, referenceNumber) =>
              val answers = emptyUserAnswers.setValue(ConveyanceReferenceNumberPage(index), referenceNumber)
              val helper  = new TransportMeansCheckYourAnswersHelper(answers, mode)
              val result  = helper.conveyanceReferenceNumber(index)

              result mustBe Some(
                SummaryListRow(
                  key = Key("Conveyance reference number".toText),
                  value = Value(s"$referenceNumber".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href =
                            controllers.transport.transportMeans.active.routes.ConveyanceReferenceNumberController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("conveyance reference number for the departure means of transport"),
                          attributes = Map("id" -> "change-transport-means-conveyance-reference-number")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

  }

}
