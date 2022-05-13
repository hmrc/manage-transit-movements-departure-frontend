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

package utils

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.{DeclarationType, LocalReferenceNumber, Mode, ProcedureType}
import models.reference.CustomsOffice
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{DeclarationTypePage, OfficeOfDeparturePage, ProcedureTypePage}
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class PreTaskListCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper with ScalaCheckPropertyChecks with Generators {

  "PreTaskListCheckYourAnswersHelper" - {

    "localReferenceNumber" - {
      "must return row" in {
        forAll(arbitrary[LocalReferenceNumber], arbitrary[Mode]) {
          (lrn, mode) =>
            val answers = emptyUserAnswers.copy(lrn = lrn)

            val helper = new PreTaskListCheckYourAnswersHelper(answers, mode)
            val result = helper.localReferenceNumber

            result mustBe SummaryListRow(
              key = Key("Local reference number".toText, classes = "govuk-!-width-one-half"),
              value = Value(s"$lrn".toText),
              actions = Some(
                Actions(items =
                  List(
                    ActionItem(
                      content = "Change".toText,
                      href = controllers.routes.LocalReferenceNumberController.onPageLoad().url,
                      visuallyHiddenText = Some("the local reference number"),
                      attributes = Map()
                    )
                  )
                )
              )
            )
        }
      }
    }

    "officeOfDeparture" - {
      "must return None" - {
        "when OfficeOfDeparturePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new PreTaskListCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.officeOfDeparture
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when OfficeOfDeparturePage defined" in {
          forAll(arbitrary[CustomsOffice], arbitrary[Mode]) {
            (customsOffice, mode) =>
              val answers = emptyUserAnswers.unsafeSetVal(OfficeOfDeparturePage)(customsOffice)

              val helper = new PreTaskListCheckYourAnswersHelper(answers, mode)
              val result = helper.officeOfDeparture

              result mustBe Some(
                SummaryListRow(
                  key = Key("Office of departure".toText, classes = "govuk-!-width-one-half"),
                  value = Value(s"$customsOffice".toText),
                  actions = Some(
                    Actions(items =
                      List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.routes.OfficeOfDepartureController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the office of departure"),
                          attributes = Map()
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

    "procedureType" - {
      "must return None" - {
        "when ProcedureTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new PreTaskListCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.procedureType
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ProcedureTypePage defined" in {
          forAll(arbitrary[ProcedureType], arbitrary[Mode]) {
            (procedureType, mode) =>
              val answers = emptyUserAnswers.unsafeSetVal(ProcedureTypePage)(procedureType)

              val helper = new PreTaskListCheckYourAnswersHelper(answers, mode)
              val result = helper.procedureType

              result mustBe Some(
                SummaryListRow(
                  key = Key("Type of procedure".toText, classes = "govuk-!-width-one-half"),
                  value = Value(messages(s"procedureType.$procedureType").toText),
                  actions = Some(
                    Actions(items =
                      List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.routes.ProcedureTypeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the type of procedure"),
                          attributes = Map()
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

    "declarationType" - {
      "must return None" - {
        "when DeclarationTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new PreTaskListCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.declarationType
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when DeclarationTypePage defined" in {
          forAll(arbitrary[DeclarationType], arbitrary[Mode]) {
            (declarationType, mode) =>
              val answers = emptyUserAnswers.unsafeSetVal(DeclarationTypePage)(declarationType)

              val helper = new PreTaskListCheckYourAnswersHelper(answers, mode)
              val result = helper.declarationType

              result mustBe Some(
                SummaryListRow(
                  key = Key("Declaration type".toText, classes = "govuk-!-width-one-half"),
                  value = Value(messages(s"declarationType.$declarationType").toText),
                  actions = Some(
                    Actions(items =
                      List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.routes.DeclarationTypeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the declaration type"),
                          attributes = Map()
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
