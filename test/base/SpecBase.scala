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

package base

import models.{EoriNumber, LocalReferenceNumber, RichJsObject, SubmissionState, UserAnswers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import pages.{QuestionPage, ReadOnlyPage}
import play.api.libs.json.{Format, JsResultException, Json, Reads}
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{ActionItem, Content, Key, Value}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

trait SpecBase extends AnyFreeSpec with Matchers with OptionValues with EitherValues with TryValues with ScalaFutures with MockitoSugar {

  val eoriNumber: EoriNumber    = EoriNumber("GB1234567891234")
  val lrn: LocalReferenceNumber = LocalReferenceNumber("ABCD1234567890123").get
  val uuid                      = "2e8ede47-dbfb-44ea-a1e3-6c57b1fe6fe2"

  def fakeRequest: FakeRequest[AnyContent] = FakeRequest("", "")

  val emptyUserAnswers: UserAnswers = UserAnswers(lrn, eoriNumber, Json.obj(), status = SubmissionState.NotSubmitted)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit class RichUserAnswers(userAnswers: UserAnswers) {

    def getValue[T](page: QuestionPage[T])(implicit rds: Reads[T]): T =
      userAnswers.get(page).value

    def setValue[T](page: QuestionPage[T], value: T)(implicit format: Format[T]): UserAnswers =
      userAnswers.set(page, value).success.value

    def setValue[T](page: ReadOnlyPage[T], value: T)(implicit format: Format[T]): UserAnswers =
      userAnswers.data
        .setObject(page.path, Json.toJson(value))
        .fold(
          errors => throw JsResultException(errors),
          jsValue => userAnswers.copy(data = jsValue)
        )

    def setValue[T](page: QuestionPage[T], value: Option[T])(implicit format: Format[T]): UserAnswers =
      value.map(setValue(page, _)).getOrElse(userAnswers)

    def setValue[T](page: QuestionPage[T], f: UserAnswers => T)(implicit format: Format[T]): UserAnswers =
      setValue(page, f(userAnswers))

    def removeValue(page: QuestionPage[?]): UserAnswers =
      userAnswers.remove(page).success.value
  }

  implicit class RichContent(c: Content) {
    def value: String = c.asHtml.toString()
  }

  implicit class RichKey(k: Key) {
    def value: String = k.content.value
  }

  implicit class RichValue(v: Value) {
    def value: String = v.content.value
  }

  implicit class RichAction(ai: ActionItem) {
    def id: String = ai.attributes.get("id").value
  }

  def response(status: Int): Future[HttpResponse] = Future.successful(HttpResponse(status, ""))
}
