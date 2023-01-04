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

package navigation.transport

import models.domain.UserAnswersReader
import models.journeyDomain.transport.TransportMeansActiveListDomain
import models.{CheckMode, Mode, NormalMode}
import navigation.UserAnswersNavigator

import javax.inject.{Inject, Singleton}

@Singleton
class TransportMeansActiveListNavigatorProviderImpl @Inject() () extends TransportMeansActiveListNavigatorProvider {

  override def apply(mode: Mode): UserAnswersNavigator =
    mode match {
      case NormalMode => new TransportMeansActiveListNavigator(mode)
      case CheckMode  => new TransportMeansNavigator(mode)
    }
}

trait TransportMeansActiveListNavigatorProvider {
  def apply(mode: Mode): UserAnswersNavigator
}

class TransportMeansActiveListNavigator(override val mode: Mode) extends UserAnswersNavigator {

  override type T = TransportMeansActiveListDomain

  implicit override val reader: UserAnswersReader[TransportMeansActiveListDomain] =
    TransportMeansActiveListDomain.userAnswersReader
}
