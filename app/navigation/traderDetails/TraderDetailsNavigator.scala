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

package navigation.traderDetails

import models.Mode
import models.domain.UserAnswersReader
import models.journeyDomain.traderDetails.TraderDetailsDomain
import navigation.{PreTaskListNavigator, UserAnswersNavigator}

import javax.inject.{Inject, Singleton}

@Singleton
class TraderDetailsNavigatorProviderImpl @Inject() () extends TraderDetailsNavigatorProvider {

  override def apply(mode: Mode): UserAnswersNavigator =
    new PreTaskListNavigator(mode)
}

trait TraderDetailsNavigatorProvider {
  def apply(mode: Mode): UserAnswersNavigator
}

class TraderDetailsNavigator(override val mode: Mode) extends UserAnswersNavigator {

  override type T = TraderDetailsDomain

  implicit override val reader: UserAnswersReader[TraderDetailsDomain] =
    TraderDetailsDomain.userAnswersParser
}
