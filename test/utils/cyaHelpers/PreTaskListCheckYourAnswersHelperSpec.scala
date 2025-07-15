/*
 * Copyright 2024 HM Revenue & Customs
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

package utils.cyaHelpers

import base.SpecBase
import controllers.preTaskList.routes
import generators.Generators
import models.reference.{AdditionalDeclarationType, CustomsOffice, DeclarationType, SecurityType}
import models.{LocalReferenceNumber, Mode, ProcedureType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class PreTaskListCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "PreTaskListCheckYourAnswersHelper" - {

    "localReferenceNumber" - {
      "must return row" in {
        forAll(arbitrary[LocalReferenceNumber], arbitrary[Mode]) {
          (lrn, mode) =>
            val answers = emptyUserAnswers.copy(lrn = lrn)

            val helper = new PreTaskListCheckYourAnswersHelper(answers, mode)
            val result = helper.localReferenceNumber

            result mustEqual SummaryListRow(
              key = Key("Local Reference Number (LRN)".toText),
              value = Value(s"$lrn".toText),
              actions = Some(
                Actions(
                  items = List(
                    ActionItem(
                      content = "Change".toText,
                      href = routes.LocalReferenceNumberController.onPageReload(lrn).url,
                      visuallyHiddenText = Some("local reference number"),
                      attributes = Map()
                    )
                  )
                )
              )
            )
        }
      }
    }

    "additionalDeclarationType" - {
      "must return None" - {
        s"when $AdditionalDeclarationTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new PreTaskListCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.additionalDeclarationType
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $AdditionalDeclarationTypePage defined" in {
          forAll(arbitrary[AdditionalDeclarationType], arbitrary[Mode]) {
            (additionalDeclarationType, mode) =>
              val answers = emptyUserAnswers.setValue(AdditionalDeclarationTypePage, additionalDeclarationType)

              val helper = new PreTaskListCheckYourAnswersHelper(answers, mode)
              val result = helper.additionalDeclarationType

              result.value mustEqual SummaryListRow(
                key = Key("Is this a standard or pre-lodged declaration?".toText),
                value = Value(additionalDeclarationType.asString.toText),
                actions = Some(
                  Actions(
                    items = List(
                      ActionItem(
                        content = "Change".toText,
                        href = routes.AdditionalDeclarationTypeController.onPageLoad(answers.lrn, mode).url,
                        visuallyHiddenText = Some("whether this is a standard or pre-lodged declaration"),
                        attributes = Map()
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "officeOfDeparture" - {
      "must return None" - {
        s"when $OfficeOfDeparturePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new PreTaskListCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.officeOfDeparture
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $OfficeOfDeparturePage defined" in {
          forAll(arbitrary[CustomsOffice], arbitrary[Mode]) {
            (customsOffice, mode) =>
              val answers = emptyUserAnswers.setValue(OfficeOfDeparturePage, customsOffice)

              val helper = new PreTaskListCheckYourAnswersHelper(answers, mode)
              val result = helper.officeOfDeparture

              result.value mustEqual SummaryListRow(
                key = Key("Office of departure".toText),
                value = Value(s"$customsOffice".toText),
                actions = Some(
                  Actions(
                    items = List(
                      ActionItem(
                        content = "Change".toText,
                        href = routes.OfficeOfDepartureController.onPageLoad(answers.lrn, mode).url,
                        visuallyHiddenText = Some("office of departure"),
                        attributes = Map()
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "procedureType" - {
      "must return None" - {
        s"when $ProcedureTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new PreTaskListCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.procedureType
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $ProcedureTypePage defined" in {
          forAll(arbitrary[ProcedureType], arbitrary[Mode]) {
            (procedureType, mode) =>
              val answers = emptyUserAnswers.setValue(ProcedureTypePage, procedureType)

              val helper = new PreTaskListCheckYourAnswersHelper(answers, mode)
              val result = helper.procedureType

              result.value mustEqual SummaryListRow(
                key = Key("Type of procedure".toText),
                value = Value(messages(s"procedureType.$procedureType").toText),
                actions = Some(
                  Actions(
                    items = List(
                      ActionItem(
                        content = "Change".toText,
                        href = routes.ProcedureTypeController.onPageLoad(answers.lrn, mode).url,
                        visuallyHiddenText = Some("type of procedure"),
                        attributes = Map()
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "declarationType" - {
      "must return None" - {
        s"when $DeclarationTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new PreTaskListCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.declarationType
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $DeclarationTypePage defined" in {
          forAll(arbitrary[DeclarationType], arbitrary[Mode]) {
            (declarationType, mode) =>
              val answers = emptyUserAnswers.setValue(DeclarationTypePage, declarationType)

              val helper = new PreTaskListCheckYourAnswersHelper(answers, mode)
              val result = helper.declarationType

              result.value mustEqual SummaryListRow(
                key = Key("Type of declaration".toText),
                value = Value(declarationType.asString.toText),
                actions = Some(
                  Actions(
                    items = List(
                      ActionItem(
                        content = "Change".toText,
                        href = routes.DeclarationTypeController.onPageLoad(answers.lrn, mode).url,
                        visuallyHiddenText = Some("type of declaration"),
                        attributes = Map()
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "tirCarnet" - {
      "must return None" - {
        s"when $TIRCarnetReferencePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new PreTaskListCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.tirCarnet
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $TIRCarnetReferencePage defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (tirCarnetReference, mode) =>
              val answers = emptyUserAnswers.setValue(TIRCarnetReferencePage, tirCarnetReference)

              val helper = new PreTaskListCheckYourAnswersHelper(answers, mode)
              val result = helper.tirCarnet

              result.value mustEqual SummaryListRow(
                key = Key("TIR carnet reference".toText),
                value = Value(tirCarnetReference.toText),
                actions = Some(
                  Actions(
                    items = List(
                      ActionItem(
                        content = "Change".toText,
                        href = routes.TIRCarnetReferenceController.onPageLoad(answers.lrn, mode).url,
                        visuallyHiddenText = Some("TIR carnet reference"),
                        attributes = Map()
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "securityType" - {
      "must return None" - {
        s"when $SecurityDetailsTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new PreTaskListCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.securityType
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        s"when $SecurityDetailsTypePage defined" in {
          forAll(arbitrary[SecurityType], arbitrary[Mode]) {
            (securityDetailsType, mode) =>
              val answers = emptyUserAnswers.setValue(SecurityDetailsTypePage, securityDetailsType)

              val helper = new PreTaskListCheckYourAnswersHelper(answers, mode)
              val result = helper.securityType

              result.value mustEqual SummaryListRow(
                key = Key("Safety and security details".toText),
                value = Value(securityDetailsType.asString.toText),
                actions = Some(
                  Actions(
                    items = List(
                      ActionItem(
                        content = "Change".toText,
                        href = routes.SecurityDetailsTypeController.onPageLoad(answers.lrn, mode).url,
                        visuallyHiddenText = Some("safety and security details"),
                        attributes = Map()
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
