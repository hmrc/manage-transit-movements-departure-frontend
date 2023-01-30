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

package config

import com.google.inject.AbstractModule
import controllers.actions._
import navigation._
import navigation.guaranteeDetails._
import navigation.routeDetails._
import navigation.traderDetails._
import navigation.transport._

import java.time.Clock

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[PreTaskListNavigatorProvider]).to(classOf[PreTaskListNavigatorProviderImpl])

    bind(classOf[TraderDetailsNavigatorProvider]).to(classOf[TraderDetailsNavigatorProviderImpl])

    bind(classOf[GuaranteeDetailsNavigatorProvider]).to(classOf[GuaranteeDetailsNavigatorProviderImpl])
    bind(classOf[GuaranteeNavigatorProvider]).to(classOf[GuaranteeNavigatorProviderImpl])

    bind(classOf[RouteDetailsNavigatorProvider]).to(classOf[RouteDetailsNavigatorProviderImpl])
    bind(classOf[RoutingNavigatorProvider]).to(classOf[RoutingNavigatorProviderImpl])
    bind(classOf[CountryOfRoutingNavigatorProvider]).to(classOf[CountryOfRoutingNavigatorProviderImpl])
    bind(classOf[TransitNavigatorProvider]).to(classOf[TransitNavigatorProviderImpl])
    bind(classOf[OfficeOfTransitNavigatorProvider]).to(classOf[OfficeOfTransitNavigatorProviderImpl])
    bind(classOf[ExitNavigatorProvider]).to(classOf[ExitNavigatorProviderImpl])
    bind(classOf[OfficeOfExitNavigatorProvider]).to(classOf[OfficeOfExitNavigatorProviderImpl])
    bind(classOf[LocationOfGoodsNavigatorProvider]).to(classOf[LocationOfGoodsNavigatorProviderImpl])
    bind(classOf[LoadingAndUnloadingNavigatorProvider]).to(classOf[LoadingAndUnloadingNavigatorProviderImpl])

    bind(classOf[TransportNavigatorProvider]).to(classOf[TransportNavigatorProviderImpl])
    bind(classOf[TransportMeansNavigatorProvider]).to(classOf[TransportMeansNavigatorProviderImpl])
    bind(classOf[TransportMeansActiveNavigatorProvider]).to(classOf[TransportMeansActiveNavigatorProviderImpl])
    bind(classOf[TransportMeansActiveListNavigatorProvider]).to(classOf[TransportMeansActiveListNavigatorProviderImpl])
    bind(classOf[SupplyChainActorNavigatorProvider]).to(classOf[SupplyChainActorNavigatorProviderImpl])
    bind(classOf[AuthorisationNavigatorProvider]).to(classOf[AuthorisationNavigatorProviderImpl])

    bind(classOf[IdentifierAction]).to(classOf[AuthenticatedIdentifierAction])
    bind(classOf[DataRetrievalActionProvider]).to(classOf[DataRetrievalActionProviderImpl])
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl])
    bind(classOf[PreTaskListCompletedAction]).to(classOf[PreTaskListCompletedActionImpl])
    bind(classOf[CheckDependentTaskCompletedActionProvider]).to(classOf[CheckDependentTaskCompletedActionProviderImpl])
    bind(classOf[SpecificDataRequiredActionProvider]).to(classOf[SpecificDataRequiredActionImpl]).asEagerSingleton()

    bind(classOf[RenderConfig]).to(classOf[RenderConfigImpl]).asEagerSingleton()

    bind(classOf[Clock]).toInstance(Clock.systemUTC)
  }
}
