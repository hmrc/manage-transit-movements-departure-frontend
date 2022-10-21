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
import models.{Index, Mode, UserAnswers}
import navigation._
import navigation.routeDetails._
import navigation.traderDetails.TraderDetailsNavigatorProvider
import navigation.transport.PreRequisitesNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, TestSuite}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.{GuiceFakeApplicationFactory, GuiceOneAppPerSuite}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait AppWithDefaultMockFixtures extends BeforeAndAfterEach with GuiceOneAppPerSuite with GuiceFakeApplicationFactory with MockitoSugar {
  self: TestSuite =>

  override def beforeEach(): Unit = {
    reset(mockSessionRepository); reset(mockDataRetrievalActionProvider)
  }

  final val mockSessionRepository: SessionRepository                     = mock[SessionRepository]
  final val mockDataRetrievalActionProvider: DataRetrievalActionProvider = mock[DataRetrievalActionProvider]

  final override def fakeApplication(): Application =
    guiceApplicationBuilder()
      .build()

  protected def setExistingUserAnswers(userAnswers: UserAnswers): Unit = setUserAnswers(Some(userAnswers))

  protected def setNoExistingUserAnswers(): Unit = setUserAnswers(None)

  private def setUserAnswers(userAnswers: Option[UserAnswers]): Unit =
    when(mockDataRetrievalActionProvider.apply(any())) thenReturn new FakeDataRetrievalAction(userAnswers)

  protected val onwardRoute: Call = Call("GET", "/foo")

  protected val fakeNavigator: Navigator = new FakeNavigator(onwardRoute)

  protected val fakePreTaskListNavigatorProvider: PreTaskListNavigatorProvider =
    (mode: Mode) => new FakePreTaskListNavigator(onwardRoute, mode)

  protected val fakeGuaranteeNavigatorProvider: GuaranteeNavigatorProvider =
    (mode: Mode, index: Index) => new FakeGuaranteeNavigator(onwardRoute, mode, index)

  protected val fakeTraderDetailsNavigatorProvider: TraderDetailsNavigatorProvider =
    (mode: Mode) => new FakeTraderDetailsNavigator(onwardRoute, mode)

  protected val fakeRouteDetailsNavigatorProvider: RouteDetailsNavigatorProvider =
    new RouteDetailsNavigatorProvider {

      override def apply(mode: Mode)(implicit hc: HeaderCarrier): Future[RouteDetailsNavigator] =
        Future.successful(new FakeRouteDetailsNavigator(onwardRoute, mode))
    }

  val fakeRoutingNavigatorProvider: RoutingNavigatorProvider =
    new RoutingNavigatorProvider {

      override def apply(mode: Mode)(implicit hc: HeaderCarrier): Future[RoutingNavigator] =
        Future.successful(new FakeRoutingNavigator(onwardRoute, mode))
    }

  protected val fakeCountryOfRoutingNavigatorProvider: CountryOfRoutingNavigatorProvider =
    new CountryOfRoutingNavigatorProvider {

      override def apply(mode: Mode, index: Index)(implicit hc: HeaderCarrier): Future[CountryOfRoutingNavigator] =
        Future.successful(new FakeCountryOfRoutingNavigator(onwardRoute, mode, index))
    }

  val fakeTransitNavigatorProvider: TransitNavigatorProvider =
    new TransitNavigatorProvider {

      override def apply(mode: Mode)(implicit hc: HeaderCarrier): Future[TransitNavigator] =
        Future.successful(new FakeTransitNavigator(onwardRoute, mode))
    }

  protected val fakeOfficeOfTransitNavigatorProvider: OfficeOfTransitNavigatorProvider =
    new OfficeOfTransitNavigatorProvider {

      override def apply(mode: Mode, index: Index)(implicit hc: HeaderCarrier): Future[OfficeOfTransitNavigator] =
        Future.successful(new FakeOfficeOfTransitNavigator(onwardRoute, mode, index))
    }

  val fakeExitNavigatorProvider: ExitNavigatorProvider =
    new ExitNavigatorProvider {

      override def apply(mode: Mode)(implicit hc: HeaderCarrier): Future[ExitNavigator] =
        Future.successful(new FakeExitNavigator(onwardRoute, mode))
    }

  protected val fakeOfficeOfExitNavigatorProvider: OfficeOfExitNavigatorProvider =
    new OfficeOfExitNavigatorProvider {

      override def apply(mode: Mode, index: Index)(implicit hc: HeaderCarrier): Future[OfficeOfExitNavigator] =
        Future.successful(new FakeOfficeOfExitNavigator(onwardRoute, mode, index))
    }

  protected val fakeLocationOfGoodsNavigatorProvider: LocationOfGoodsNavigatorProvider =
    new LocationOfGoodsNavigatorProvider {

      override def apply(mode: Mode)(implicit hc: HeaderCarrier): Future[LocationOfGoodsNavigator] =
        Future.successful(new FakeLocationOfGoodsNavigator(onwardRoute, mode))
    }

  protected val fakeLoadingNavigatorProvider: LoadingAndUnloadingNavigatorProvider =
    new LoadingAndUnloadingNavigatorProvider {

      override def apply(mode: Mode)(implicit hc: HeaderCarrier): Future[LoadingAndUnloadingNavigator] =
        Future.successful(new FakeLoadingAndUnloadingNavigator(onwardRoute, mode))
    }

  protected val fakePreRequisitesNavigatorProvider: PreRequisitesNavigatorProvider =
    (mode: Mode) => new FakePreRequisitesNavigator(onwardRoute, mode)

  def guiceApplicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[DataRetrievalActionProvider].toInstance(mockDataRetrievalActionProvider)
      )
}
