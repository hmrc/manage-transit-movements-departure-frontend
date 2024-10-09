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

package config

import com.google.inject.AbstractModule
import controllers.actions._
import navigation._

import java.time.Clock

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[PreTaskListNavigatorProvider]).to(classOf[PreTaskListNavigatorProviderImpl])

    bind(classOf[IdentifierAction]).to(classOf[AuthenticatedIdentifierAction])
    bind(classOf[DataRetrievalActionProvider]).to(classOf[DataRetrievalActionProviderImpl])
    bind(classOf[DataRequiredActionProvider]).to(classOf[DataRequiredActionImpl])
    bind(classOf[PreTaskListCompletedAction]).to(classOf[PreTaskListCompletedActionImpl])
    bind(classOf[DependentTaskAction]).to(classOf[DependentTaskActionImpl])
    bind(classOf[SpecificDataRequiredActionProvider]).to(classOf[SpecificDataRequiredActionImpl]).asEagerSingleton()

    bind(classOf[RenderConfig]).to(classOf[RenderConfigImpl]).asEagerSingleton()

    bind(classOf[Clock]).toInstance(Clock.systemUTC)
  }
}
