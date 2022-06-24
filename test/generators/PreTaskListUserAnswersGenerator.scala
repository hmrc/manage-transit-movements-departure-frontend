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

package generators

import models.DeclarationType.Option4
import models.ProcedureType.Normal
import models.{DeclarationType, ProcedureType, UserAnswers}
import org.scalacheck.{Arbitrary, Gen}
import pages.preTaskList._
import play.api.libs.json.{JsBoolean, Json}

trait PreTaskListUserAnswersGenerator extends UserAnswersGenerator {
  self: Generators =>

  lazy val arbitraryPreTaskListAnswers: Gen[UserAnswers] = Gen.oneOf(
    arbitraryPreTaskListAnswersWithTir,
    arbitraryPreTaskListAnswersWithoutTir
  )

  lazy val arbitraryPreTaskListAnswersWithTir: Gen[UserAnswers] = arbitraryUserAnswers(
    arbitraryXiOfficeOfDepartureUserAnswersEntry.arbitrary ::
      Arbitrary((ProcedureTypePage, Json.toJson[ProcedureType](Normal))).arbitrary ::
      Arbitrary((DeclarationTypePage, Json.toJson[DeclarationType](Option4))).arbitrary ::
      arbitraryTIRCarnetReferenceUserAnswersEntry.arbitrary ::
      arbitraryAddSecurityDetailsUserAnswersEntry.arbitrary ::
      Arbitrary((DetailsConfirmedPage, JsBoolean(true))).arbitrary ::
      Nil
  )

  lazy val arbitraryPreTaskListAnswersWithoutTir: Gen[UserAnswers] = arbitraryUserAnswers(
    arbitraryOfficeOfDepartureUserAnswersEntry.arbitrary ::
      arbitraryProcedureTypeUserAnswersEntry.arbitrary ::
      arbitraryNonOption4DeclarationTypeUserAnswersEntry.arbitrary ::
      arbitraryAddSecurityDetailsUserAnswersEntry.arbitrary ::
      Arbitrary((DetailsConfirmedPage, JsBoolean(true))).arbitrary ::
      Nil
  )
}
