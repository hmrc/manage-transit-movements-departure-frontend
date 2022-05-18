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

package viewModels

import base.SpecBase
import models.reference.{CountryCode, CustomsOffice}
import models.{DeclarationType, LocalReferenceNumber, ProcedureType, SecurityDetailsType}
import pages.preTaskList._

class PreTaskListViewModelSpec extends SpecBase {

  "apply" - {
    "when user answers empty" - {
      "must only return local reference number row" in {
        val answers = emptyUserAnswers
          .copy(lrn = LocalReferenceNumber("1234567890").get)

        val section = new PreTaskListViewModel().apply(answers)

        section.sectionTitle mustNot be(defined)
        section.rows.length mustBe 1
        section.rows.head.value.content.asHtml.toString() mustBe "1234567890"
      }
    }

    "when user answers populated" - {
      "must return row for each answer" in {
        val answers = emptyUserAnswers
          .copy(lrn = LocalReferenceNumber("1234567890").get)
          .setValue(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("code"), None))
          .setValue(ProcedureTypePage, ProcedureType.Normal)
          .setValue(DeclarationTypePage, DeclarationType.Option4)
          .setValue(TIRCarnetReferencePage, "tir carnet reference")
          .setValue(SecurityDetailsTypePage, SecurityDetailsType.EntrySummaryDeclarationSecurityDetails)

        val section = new PreTaskListViewModel().apply(answers)

        section.sectionTitle mustNot be(defined)
        section.rows.length mustBe 6
        section.rows.head.value.content.asHtml.toString() mustBe "1234567890"
        section.rows(1).value.content.asHtml.toString() mustBe "name (id)"
        section.rows(2).value.content.asHtml.toString() mustBe "Normal (customs-approved location)"
        section.rows(3).value.content.asHtml.toString() mustBe "TIR (goods moving under the cover of TIR Carnet)"
        section.rows(4).value.content.asHtml.toString() mustBe "tir carnet reference"
        section.rows(5).value.content.asHtml.toString() mustBe "Entry summary declaration (ENS)"
      }
    }
  }
}
