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

package generators

import models._
import models.reference._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json._
import queries.Gettable

trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generatePreTaskListAnswer

  private def generatePreTaskListAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.preTaskList._
    {
      case OfficeOfDeparturePage   => arbitrary[CustomsOffice](arbitraryOfficeOfDeparture).map(Json.toJson(_))
      case ProcedureTypePage       => arbitrary[ProcedureType].map(Json.toJson(_))
      case DeclarationTypePage     => arbitrary[DeclarationType].map(Json.toJson(_))
      case TIRCarnetReferencePage  => Gen.alphaNumStr.map(JsString)
      case SecurityDetailsTypePage => arbitrary[SecurityType].map(Json.toJson(_))
      case DetailsConfirmedPage    => Gen.const(true).map(JsBoolean)
    }
  }
}
