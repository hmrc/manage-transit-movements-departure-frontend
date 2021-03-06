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

package config

import com.google.inject.AbstractModule
import controllers.actions._
import navigation._
import navigation.annotations._
import navigation.annotations.routeDetails._
import navigation.annotations.traderDetails._
import navigation.routeDetails._
import navigation.traderDetails._

import java.time.Clock

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[Navigator]).annotatedWith(classOf[PreTaskListDetails]).to(classOf[PreTaskListNavigator])

    bind(classOf[Navigator]).annotatedWith(classOf[TraderDetails]).to(classOf[TraderDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[HolderOfTransit]).to(classOf[HolderOfTransitNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[Representative]).to(classOf[RepresentativeNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[Consignment]).to(classOf[ConsignmentNavigator])

    bind(classOf[GuaranteeNavigatorProvider]).to(classOf[GuaranteeNavigatorProviderImpl])

    bind(classOf[Navigator]).annotatedWith(classOf[Routing]).to(classOf[RoutingNavigator])
    bind(classOf[CountryOfRoutingNavigatorProvider]).to(classOf[CountryOfRoutingNavigatorProviderImpl])

    bind(classOf[IdentifierAction]).to(classOf[AuthenticatedIdentifierAction])
    bind(classOf[DataRetrievalActionProvider]).to(classOf[DataRetrievalActionProviderImpl])
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl])
    bind(classOf[CheckTaskAlreadyCompletedActionProvider]).to(classOf[CheckTaskAlreadyCompletedActionProviderImpl])
    bind(classOf[CheckDependentTaskCompletedActionProvider]).to(classOf[CheckDependentTaskCompletedActionProviderImpl])
    bind(classOf[SpecificDataRequiredActionProvider]).to(classOf[SpecificDataRequiredActionImpl]).asEagerSingleton()

    bind(classOf[RenderConfig]).to(classOf[RenderConfigImpl]).asEagerSingleton()

    bind(classOf[Clock]).toInstance(Clock.systemUTC)
  }
}
