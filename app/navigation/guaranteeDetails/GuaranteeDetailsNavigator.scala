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

package navigation.guaranteeDetails

import models._
import models.domain.UserAnswersReader
import models.journeyDomain.guaranteeDetails.GuaranteeDetailsDomain
import navigation.UserAnswersNavigator

import javax.inject.{Inject, Singleton}

@Singleton
class GuaranteeDetailsNavigatorProviderImpl @Inject() () extends GuaranteeDetailsNavigatorProvider {

  def apply(mode: Mode): UserAnswersNavigator =
    new GuaranteeDetailsNavigator(mode)
}

trait GuaranteeDetailsNavigatorProvider {

  def apply(mode: Mode): UserAnswersNavigator
}

class GuaranteeDetailsNavigator(override val mode: Mode) extends UserAnswersNavigator {

  override type T = GuaranteeDetailsDomain

  implicit override val reader: UserAnswersReader[GuaranteeDetailsDomain] =
    GuaranteeDetailsDomain.userAnswersReader
}
