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

import controllers.actions._
import models.{Index, UserAnswers}
import navigation.routeDetails.CountryOfRoutingNavigatorProvider
import navigation._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.{BeforeAndAfterEach, TestSuite}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.{GuiceFakeApplicationFactory, GuiceOneAppPerSuite}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import repositories.SessionRepository

trait AppWithDefaultMockFixtures extends BeforeAndAfterEach with GuiceOneAppPerSuite with GuiceFakeApplicationFactory with MockitoSugar {
  self: TestSuite =>

  override def beforeEach(): Unit =
    Mockito.reset(
      mockSessionRepository,
      mockDataRetrievalActionProvider
    )

  final val mockSessionRepository: SessionRepository = mock[SessionRepository]
  final val mockDataRetrievalActionProvider          = mock[DataRetrievalActionProvider]

  final override def fakeApplication(): Application =
    guiceApplicationBuilder()
      .build()

  protected def setExistingUserAnswers(userAnswers: UserAnswers): Unit = setUserAnswers(Some(userAnswers))

  protected def setNoExistingUserAnswers(): Unit = setUserAnswers(None)

  private def setUserAnswers(userAnswers: Option[UserAnswers]): Unit =
    when(mockDataRetrievalActionProvider.apply(any())) thenReturn new FakeDataRetrievalAction(userAnswers)

  protected val onwardRoute: Call = Call("GET", "/foo")

  protected val fakeNavigator: Navigator = new FakeNavigator(onwardRoute)

  protected val fakeGuaranteeNavigatorProvider: GuaranteeNavigatorProvider =
    (index: Index) => new FakeGuaranteeNavigator(onwardRoute, index)

  protected val fakeCountryOfRoutingNavigatorProvider: CountryOfRoutingNavigatorProvider =
    (index: Index) => new FakeCountryOfRoutingNavigator(onwardRoute, index)

  def guiceApplicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[DataRetrievalActionProvider].toInstance(mockDataRetrievalActionProvider)
      )
}
