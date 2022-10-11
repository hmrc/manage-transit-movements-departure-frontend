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

package navigation

import models.Mode
import models.domain.UserAnswersReader
import models.journeyDomain.PreTaskListDomain

import javax.inject.{Inject, Singleton}

@Singleton
class PreTaskListNavigatorProviderImpl @Inject() () extends PreTaskListNavigatorProvider {

  override def apply(mode: Mode): UserAnswersNavigator =
    new PreTaskListNavigator(mode)
}

trait PreTaskListNavigatorProvider {
  def apply(mode: Mode): UserAnswersNavigator
}

class PreTaskListNavigator(override val mode: Mode) extends UserAnswersNavigator {

  override type T = PreTaskListDomain

  implicit override val reader: UserAnswersReader[PreTaskListDomain] =
    PreTaskListDomain.reader
}
