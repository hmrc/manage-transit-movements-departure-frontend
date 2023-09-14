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

package viewModels.preTaskList

import base.SpecBase
import generators.Generators
import models.reference.CustomsOffice
import models.{AdditionalDeclarationType, DeclarationType, LocalReferenceNumber, ProcedureType, SecurityDetailsType}
import pages.preTaskList._
import viewModels.preTaskList.PreTaskListViewModel.PreTaskListViewModelProvider

class PreTaskListViewModelSpec extends SpecBase with Generators {

  "apply" - {
    "when user answers empty" - {
      "must only return local reference number row" in {
        val answers = emptyUserAnswers
          .copy(lrn = LocalReferenceNumber("1234567890").get)

        val viewModelProvider = injector.instanceOf[PreTaskListViewModelProvider]
        val section           = viewModelProvider.apply(answers).section

        section.sectionTitle mustNot be(defined)
        section.rows.length mustBe 1
        section.rows.head.value.content.asHtml.toString() mustBe "1234567890"
      }
    }

    "when user answers populated" - {
      "must return row for each answer" in {
        val answers = emptyUserAnswers
          .copy(lrn = LocalReferenceNumber("1234567890").get)
          .setValue(AdditionalDeclarationTypePage, AdditionalDeclarationType.Standard)
          .setValue(OfficeOfDeparturePage, CustomsOffice("XI1", "name", None))
          .setValue(ProcedureTypePage, ProcedureType.Normal)
          .setValue(DeclarationTypePage, DeclarationType("TIR", "TIR carnet"))
          .setValue(TIRCarnetReferencePage, "tir carnet reference")
          .setValue(SecurityDetailsTypePage, SecurityDetailsType.EntrySummaryDeclarationSecurityDetails)

        val viewModelProvider = injector.instanceOf[PreTaskListViewModelProvider]
        val section           = viewModelProvider.apply(answers).section

        section.sectionTitle mustNot be(defined)
        section.rows.length mustBe 7
        section.rows.head.value.content.asHtml.toString() mustBe "1234567890"
        section.rows(1).value.content.asHtml.toString() mustBe "Standard - the goods have already boarded at a UK port or airport"
        section.rows(2).value.content.asHtml.toString() mustBe "name (XI1)"
        section.rows(3).value.content.asHtml.toString() mustBe "Normal - customs-approved location"
        section.rows(4).value.content.asHtml.toString() mustBe "TIR - TIR carnet"
        section.rows(5).value.content.asHtml.toString() mustBe "tir carnet reference"
        section
          .rows(6)
          .value
          .content
          .asHtml
          .toString() mustBe "Entry summary declaration (ENS) - for importing goods from a non-EU country into Great Britain or Northern Ireland. Or from Great Britain into Northern Ireland"
      }
    }
  }
}
