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

package viewModels.preTaskList

import base.SpecBase
import generators.Generators
import models.reference.{AdditionalDeclarationType, CustomsOffice, DeclarationType, SecurityType}
import models.{LocalReferenceNumber, ProcedureType}
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

        section.sectionTitle must not be defined
        section.rows.length mustEqual 1
        section.rows.head.value.value mustEqual "1234567890"
      }
    }

    "when user answers populated" - {
      "must return row for each answer" in {
        val answers = emptyUserAnswers
          .copy(lrn = LocalReferenceNumber("1234567890").get)
          .setValue(AdditionalDeclarationTypePage, AdditionalDeclarationType("A", "for a standard customs declaration (under Article 162 of the Code)"))
          .setValue(OfficeOfDeparturePage, CustomsOffice("XI1", "name", None, "XI"))
          .setValue(ProcedureTypePage, ProcedureType.Normal)
          .setValue(DeclarationTypePage, DeclarationType("TIR", "TIR carnet"))
          .setValue(TIRCarnetReferencePage, "tir carnet reference")
          .setValue(SecurityDetailsTypePage, SecurityType("0", "Not used for safety and security purposes"))

        val viewModelProvider = injector.instanceOf[PreTaskListViewModelProvider]
        val section           = viewModelProvider.apply(answers).section

        section.sectionTitle must not be defined
        section.rows.length mustEqual 7
        section.rows.head.value.content.asHtml.toString() mustEqual "1234567890"
        section.rows(1).value.value mustEqual "Standard - the goods have already boarded at a UK port or airport"
        section.rows(2).value.value mustEqual "name (XI1)"
        section.rows(3).value.value mustEqual "Normal - customs-approved location"
        section.rows(4).value.value mustEqual "TIR - goods moving under the cover of TIR carnet"
        section.rows(5).value.value mustEqual "tir carnet reference"
        section.rows(6).value.value mustEqual "No security"
      }
    }
  }
}
