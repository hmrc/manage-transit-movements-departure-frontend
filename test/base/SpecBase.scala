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

package base

import models.{EoriNumber, Index, LocalReferenceNumber, UserAnswers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.QuestionPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with OptionValues
    with EitherValues
    with GuiceOneAppPerSuite
    with TryValues
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar {

  val eoriNumber: EoriNumber    = EoriNumber("EoriNumber")
  val lrn: LocalReferenceNumber = LocalReferenceNumber("ABCD1234567890123").get

  val index: Index          = Index(0)
  val referenceIndex: Index = Index(0)
  val documentIndex: Index  = Index(0)
  val itemIndex: Index      = Index(0)
  val packageIndex: Index   = Index(0)
  val containerIndex: Index = Index(0)

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  val emptyUserAnswers: UserAnswers = UserAnswers(lrn, eoriNumber, Json.obj())

  implicit val hc: HeaderCarrier = HeaderCarrier()

  def injector: Injector = app.injector

  def messagesApi: MessagesApi    = injector.instanceOf[MessagesApi]
  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  implicit class RichUserAnswers(userAnswers: UserAnswers) {

    def getValue[T](page: QuestionPage[T])(implicit rds: Reads[T]): T =
      userAnswers.get(page).value

    def setValue[T](page: QuestionPage[T], value: T)(implicit wts: Writes[T]): UserAnswers =
      userAnswers.set(page, value).success.value

    def setValue[T](page: QuestionPage[T], value: Option[T])(implicit wts: Writes[T]): UserAnswers =
      value.map(setValue(page, _)).getOrElse(userAnswers)

    def removeValue(page: QuestionPage[_]): UserAnswers =
      userAnswers.remove(page).success.value
  }
}
