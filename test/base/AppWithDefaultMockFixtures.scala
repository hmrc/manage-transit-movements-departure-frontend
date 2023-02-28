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

package base

import controllers.actions._
import models.{CountryList, Index, Mode, UserAnswers}
import navigation._
import navigation.routeDetails._
import navigation.traderDetails.TraderDetailsNavigatorProvider
import navigation.transport._
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
import services.{CountriesService, LockService}

import scala.concurrent.Future

trait AppWithDefaultMockFixtures extends BeforeAndAfterEach with GuiceOneAppPerSuite with GuiceFakeApplicationFactory with MockitoSugar {
  self: TestSuite =>

  override def beforeEach(): Unit = {
    reset(mockSessionRepository)
    reset(mockDataRetrievalActionProvider)
    reset(mockLockService)

    when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

    when(mockCountriesService.getCountryCodesCTC()(any())).thenReturn(Future.successful(CountryList(Nil)))
    when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))
    when(mockLockService.checkLock(any())(any())).thenReturn(Future.successful(true))
  }

  final val mockSessionRepository: SessionRepository                     = mock[SessionRepository]
  final val mockDataRetrievalActionProvider: DataRetrievalActionProvider = mock[DataRetrievalActionProvider]
  final val mockCountriesService: CountriesService                       = mock[CountriesService]
  final val mockLockActionProvider: LockActionProvider                   = mock[LockActionProvider]
  final val mockLockService                                              = mock[LockService]

  final override def fakeApplication(): Application =
    guiceApplicationBuilder()
      .build()

  protected def setExistingUserAnswers(userAnswers: UserAnswers): Unit = setUserAnswers(Some(userAnswers))

  protected def setNoExistingUserAnswers(): Unit = setUserAnswers(None)

  private def setUserAnswers(userAnswers: Option[UserAnswers]): Unit = {
    when(mockLockActionProvider.apply()) thenReturn new FakeLockAction(mockLockService)
    when(mockDataRetrievalActionProvider.apply(any())) thenReturn new FakeDataRetrievalAction(userAnswers)
  }

  protected val onwardRoute: Call = Call("GET", "/foo")

  protected val fakeNavigator: Navigator = new FakeNavigator(onwardRoute)

  protected val fakePreTaskListNavigatorProvider: PreTaskListNavigatorProvider =
    (mode: Mode) => new FakePreTaskListNavigator(onwardRoute, mode)

  protected val fakeTraderDetailsNavigatorProvider: TraderDetailsNavigatorProvider =
    (mode: Mode) => new FakeTraderDetailsNavigator(onwardRoute, mode)

  protected val fakeRouteDetailsNavigatorProvider: RouteDetailsNavigatorProvider =
    (mode: Mode, _: CountryList, _: CountryList) => new FakeRouteDetailsNavigator(onwardRoute, mode)

  val fakeRoutingNavigatorProvider: RoutingNavigatorProvider =
    (mode: Mode, _: CountryList, _: CountryList) => new FakeRoutingNavigator(onwardRoute, mode)

  protected val fakeCountryOfRoutingNavigatorProvider: CountryOfRoutingNavigatorProvider =
    (mode: Mode, index: Index, _: CountryList, _: CountryList) => new FakeCountryOfRoutingNavigator(onwardRoute, mode, index)

  val fakeTransitNavigatorProvider: TransitNavigatorProvider =
    (mode: Mode, _: CountryList, _: CountryList) => new FakeTransitNavigator(onwardRoute, mode)

  protected val fakeOfficeOfTransitNavigatorProvider: OfficeOfTransitNavigatorProvider =
    (mode: Mode, index: Index, _: CountryList, _: CountryList) => new FakeOfficeOfTransitNavigator(onwardRoute, mode, index)

  val fakeExitNavigatorProvider: ExitNavigatorProvider =
    (mode: Mode, _: CountryList, _: CountryList) => new FakeExitNavigator(onwardRoute, mode)

  protected val fakeOfficeOfExitNavigatorProvider: OfficeOfExitNavigatorProvider =
    (mode: Mode, index: Index, _: CountryList, _: CountryList) => new FakeOfficeOfExitNavigator(onwardRoute, mode, index)

  protected val fakeLocationOfGoodsNavigatorProvider: LocationOfGoodsNavigatorProvider =
    (mode: Mode, _: CountryList, _: CountryList) => new FakeLocationOfGoodsNavigator(onwardRoute, mode)

  protected val fakeLoadingNavigatorProvider: LoadingAndUnloadingNavigatorProvider =
    (mode: Mode, _: CountryList, _: CountryList) => new FakeLoadingAndUnloadingNavigator(onwardRoute, mode)

  protected val fakeTransportNavigatorProvider: TransportNavigatorProvider =
    (mode: Mode) => new FakeTransportNavigator(onwardRoute, mode)

  protected val fakeTransportMeansNavigatorProvider: TransportMeansNavigatorProvider =
    (mode: Mode) => new FakeTransportMeansNavigator(onwardRoute, mode)

  protected val fakeTransportMeansActiveNavigatorProvider: TransportMeansActiveNavigatorProvider =
    (mode: Mode, index: Index) => new FakeTransportMeansActiveNavigator(onwardRoute, mode, index)

  protected val fakeTransportMeansActiveListNavigatorProvider: TransportMeansActiveListNavigatorProvider =
    (mode: Mode) => new FakeTransportMeansActiveListNavigator(onwardRoute, mode)

  protected val fakeSupplyChainActorNavigatorProvider: SupplyChainActorNavigatorProvider =
    (mode: Mode, index: Index) => new FakeSupplyChainActorNavigator(onwardRoute, mode, index)

  protected val fakeAuthorisationNavigatorProvider: AuthorisationNavigatorProvider =
    (mode: Mode, index: Index) => new FakeAuthorisationNavigator(onwardRoute, mode, index)

  protected val fakeEquipmentsNavigatorProvider: EquipmentsNavigatorProvider =
    (mode: Mode) => new FakeEquipmentsNavigator(onwardRoute, mode)

  protected val fakeEquipmentNavigatorProvider: EquipmentNavigatorProvider =
    (mode: Mode, index: Index) => new FakeEquipmentNavigator(onwardRoute, index, mode)

  protected val fakeSealNavigatorProvider: SealNavigatorProvider =
    (mode: Mode, equipmentIndex: Index, sealIndex: Index) => new FakeSealNavigator(onwardRoute, equipmentIndex, sealIndex, mode)

  protected val fakeItemNumberNavigatorProvider: ItemNumberNavigatorProvider =
    (mode: Mode, equipmentIndex: Index, sealIndex: Index) => new FakeItemNumberNavigator(onwardRoute, equipmentIndex, sealIndex, mode)

  def guiceApplicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[LockActionProvider].toInstance(mockLockActionProvider),
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[DataRetrievalActionProvider].toInstance(mockDataRetrievalActionProvider),
        bind[CountriesService].toInstance(mockCountriesService),
        bind[LockService].toInstance(mockLockService)
      )
}
