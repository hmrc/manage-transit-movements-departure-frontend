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
import navigation.annotations.addItems._
import navigation.annotations.addItemsNavigators._
import services.{DateTimeService, DateTimeServiceImpl}

import java.time.Clock

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[Navigator]).annotatedWith(classOf[PreTaskListDetails]).to(classOf[PreTaskListNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[MovementDetails]).to(classOf[MovementDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[RouteDetails]).to(classOf[RouteDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[TransportDetails]).to(classOf[TransportDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[TraderDetails]).to(classOf[TraderDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[GoodsSummary]).to(classOf[GoodsSummaryNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[GuaranteeDetails]).to(classOf[GuaranteeDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[AddItemsItemDetails]).to(classOf[AddItemsItemDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[AddItemsPackagesInfo]).to(classOf[AddItemsPackagesInfoNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[AddItemsAdminReference]).to(classOf[AddItemsAdminReferenceNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[AddItemsTraderDetails]).to(classOf[AddItemsTraderDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[AddItemsContainer]).to(classOf[AddItemsContainerNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[AddItemsSpecialMentions]).to(classOf[AddItemsSpecialMentionsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[AddItems]).to(classOf[AddItemsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[AddItemsDocument]).to(classOf[AddItemsDocumentNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[SecurityDetails]).to(classOf[AddItemsSecurityDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[TradersSecurityDetails]).to(classOf[AddItemsTradersSecurityDetailsNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[SafetyAndSecurity]).to(classOf[SafetyAndSecurityNavigator])
    bind(classOf[Navigator]).annotatedWith(classOf[SafetyAndSecurityTraderDetails]).to(classOf[SafetyAndSecurityTraderDetailsNavigator])
    bind(classOf[DataRetrievalActionProvider]).to(classOf[DataRetrievalActionProviderImpl])
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl])
    bind(classOf[IdentifierAction]).to(classOf[AuthenticatedIdentifierAction])
    bind(classOf[CheckDependentSectionAction]).to(classOf[CheckDependentSectionActionImpl])
    bind(classOf[NameRequiredAction]).to(classOf[NameRequiredActionImpl])
    bind(classOf[DateTimeService]).to(classOf[DateTimeServiceImpl]).asEagerSingleton()
    bind(classOf[RenderConfig]).to(classOf[RenderConfigImpl]).asEagerSingleton()
    bind(classOf[Clock]).toInstance(Clock.systemUTC)
  }
}
