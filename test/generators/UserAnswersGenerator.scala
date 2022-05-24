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

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends UserAnswersEntryGenerators with TryValues {
  self: Generators =>

  /**
    * The max number of QuestionPage and valid answers that are generated
    *
    * The larger this number the more QuestionPages and answers are generated
    * used in the UserAnswers that is returned. Larger values will increase test
    * runtime and should therefore have a sensible value that still provide
    * confidence that the tests are robust.
    *
    * @note The value must be greater than 0 but less than the number of elements in the
    *       class member `generators`.
    */
  val maxNumberOfGeneratedPageAnswers: Int = 1

  final val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitraryAddressUserAnswersEntry.arbitrary ::
      arbitraryNameUserAnswersEntry.arbitrary ::
      arbitraryTransitHolderEoriYesNoUserAnswersEntry.arbitrary ::
      arbitraryTIRCarnetReferenceUserAnswersEntry.arbitrary ::
      arbitraryDeclarationTypeUserAnswersEntry.arbitrary ::
      arbitraryProcedureTypeUserAnswersEntry.arbitrary ::
      arbitraryDeclarationTypeUserAnswersEntry.arbitrary ::
      arbitraryAddSecurityDetailsUserAnswersEntry.arbitrary ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id         <- arbitrary[LocalReferenceNumber]
        eoriNumber <- arbitrary[EoriNumber]
        data <- generators match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
      } yield UserAnswers(
        lrn = id,
        eoriNumber = eoriNumber,
        data = data.foldLeft(Json.obj()) {
          case (obj, (path, value)) =>
            obj.setObject(path.path, value).get
        }
      )
    }
  }
}
