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

package utils.cyaHelpers.transport.transportMeans.active

import base.SpecBase
import controllers.transport.transportMeans.active.routes
import generators.Generators
import models.Mode
import models.reference.{CustomsOffice, Nationality}
import models.transport.transportMeans.active.Identification
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.transportMeans.active._
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions}

class ActiveBorderTransportAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "ActiveBorderTransportAnswersHelper" - {

    "activeBorderIdentificationType" - {
      "must return None" - {
        "when ActiveBorderIdentificationTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ActiveBorderTransportAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.activeBorderIdentificationType
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ActiveBorderIdentificationTypePage is defined" in {
          forAll(arbitrary[Mode], arbitrary[Identification]) {
            (mode, activeIdentification) =>
              val answers = emptyUserAnswers.setValue(IdentificationPage(index), activeIdentification)
              val helper  = new ActiveBorderTransportAnswersHelper(answers, mode, index)
              val result  = helper.activeBorderIdentificationType

              result mustBe Some(
                SummaryListRow(
                  key = Key("Which identification do you want to use for this vehicle?".toText),
                  value = Value(messages(s"${"transport.transportMeans.active.identification"}.$activeIdentification").toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.IdentificationController.onPageLoad(answers.lrn, mode, index).url,
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
              val helper = new ActiveBorderTransportAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.activeBorderIdentificationNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ActiveBorderIdentificationNumberPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, identificationNumber) =>
              val answers = emptyUserAnswers.setValue(IdentificationNumberPage(index), identificationNumber)
              val helper  = new ActiveBorderTransportAnswersHelper(answers, mode, index)
              val result  = helper.activeBorderIdentificationNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("Identification number".toText),
                  value = Value(identificationNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.IdentificationNumberController.onPageLoad(answers.lrn, mode, index).url,
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
              val helper = new ActiveBorderTransportAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.activeBorderAddNationality
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when addNationality is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddNationalityYesNoPage(index), true)
              val helper  = new ActiveBorderTransportAnswersHelper(answers, mode, index)
              val result  = helper.activeBorderAddNationality

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add the registered country for this vehicle?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AddNationalityYesNoController.onPageLoad(answers.lrn, mode, index).url,
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
              val helper = new ActiveBorderTransportAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.activeBorderNationality
              result mustBe None
          }
        }

        "must return Some(Row)" - {
          "when NationalityPage is defined" in {
            forAll(arbitrary[Mode], arbitrary[Nationality]) {
              (mode, nationality) =>
                val answers = emptyUserAnswers.setValue(NationalityPage(index), nationality)
                val helper  = new ActiveBorderTransportAnswersHelper(answers, mode, index)
                val result  = helper.activeBorderNationality

                result mustBe Some(
                  SummaryListRow(
                    key = Key("Registered country".toText),
                    value = Value(s"$nationality".toText),
                    actions = Some(
                      Actions(
                        items = List(
                          ActionItem(
                            content = "Change".toText,
                            href = routes.NationalityController.onPageLoad(answers.lrn, mode, index).url,
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
              val helper = new ActiveBorderTransportAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.customsOfficeAtBorder
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when CustomsOfficeActiveBorderPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[CustomsOffice]) {
            (mode, customsOffice) =>
              val answers = emptyUserAnswers.setValue(CustomsOfficeActiveBorderPage(index), customsOffice)
              val helper  = new ActiveBorderTransportAnswersHelper(answers, mode, index)
              val result  = helper.customsOfficeAtBorder

              result mustBe Some(
                SummaryListRow(
                  key = Key("Customs office".toText),
                  value = Value(s"$customsOffice".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.CustomsOfficeActiveBorderController.onPageLoad(answers.lrn, mode, index).url,
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
              val helper = new ActiveBorderTransportAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.activeBorderConveyanceReferenceNumberYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when activeBorderConveyanceReferenceNumberYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(ConveyanceReferenceNumberYesNoPage(index), true)
              val helper  = new ActiveBorderTransportAnswersHelper(answers, mode, index)
              val result  = helper.activeBorderConveyanceReferenceNumberYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add a conveyance reference number?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.ConveyanceReferenceNumberYesNoController.onPageLoad(answers.lrn, mode, index).url,
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
              val helper = new ActiveBorderTransportAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.conveyanceReferenceNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ConveyanceReferenceNumberPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, referenceNumber) =>
              val answers = emptyUserAnswers.setValue(ConveyanceReferenceNumberPage(index), referenceNumber)
              val helper  = new ActiveBorderTransportAnswersHelper(answers, mode, index)
              val result  = helper.conveyanceReferenceNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("Conveyance reference number".toText),
                  value = Value(referenceNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.ConveyanceReferenceNumberController.onPageLoad(answers.lrn, mode, index).url,
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
