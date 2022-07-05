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

import models._
import pages.guaranteeDetails.GuaranteeTypePage

import javax.inject.{Inject, Singleton}

@Singleton
class GuaranteeDetailsNavigator @Inject() () extends Navigator {

  override val normalRoutes: RouteMapping = routes(NormalMode)

  override val checkRoutes: RouteMapping = routes(CheckMode)

  override def routes(mode: Mode): RouteMapping = {
    case GuaranteeTypePage(index) => ua => Some(controllers.routes.SessionExpiredController.onPageLoad()) //TODO change to next page when built
  }

}
