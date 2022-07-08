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

import models.domain.UserAnswersReader
import models.journeyDomain.ReaderError
import models.{RichJsObject, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends UserAnswersEntryGenerators with TryValues {
  self: Generators =>

  implicit lazy val arbitraryUserAnswers: Arbitrary[UserAnswers] =
    Arbitrary {
      arbitraryUserAnswers()
    }

  protected def arbitraryUserAnswers(gens: Gen[(QuestionPage[_], JsValue)]*): Gen[UserAnswers] = {
    import models._

    import scala.collection.convert.ImplicitConversions._

    for {
      id         <- arbitrary[LocalReferenceNumber]
      eoriNumber <- arbitrary[EoriNumber]
      data       <- Gen.sequence(gens).map(_.toList)
    } yield UserAnswers(
      lrn = id,
      eoriNumber = eoriNumber,
      data = data.foldLeft(Json.obj()) {
        case (obj, (path, value)) =>
          obj.setObject(path.path, value).get
      }
    )
  }

  protected def buildUserAnswers[T](initialUserAnswers: UserAnswers)(implicit userAnswersReader: UserAnswersReader[T]): Gen[UserAnswers] = {

    def rec(userAnswers: UserAnswers): Gen[UserAnswers] =
      UserAnswersReader[T].run(userAnswers) match {
        case Left(ReaderError(page, _)) =>
          generateAnswer
            .apply(page)
            .map {
              value =>
                userAnswers.copy(
                  data = userAnswers.data.setObject(page.path, value).getOrElse(userAnswers.data)
                )
            }
            .flatMap(rec)
        case Right(_) => Gen.const(userAnswers)
      }

    rec(initialUserAnswers)
  }
}
