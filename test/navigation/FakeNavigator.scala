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

import models.{Index, Mode, UserAnswers}
import navigation.routeDetails._
import play.api.mvc.Call

class FakeNavigator(desiredRoute: Call) extends Navigator {
  override def nextPage(userAnswers: UserAnswers, mode: Mode): Call = desiredRoute
}

class FakeGuaranteeNavigator(desiredRoute: Call, index: Index) extends GuaranteeNavigator(index) {
  override def nextPage(userAnswers: UserAnswers, mode: Mode): Call = desiredRoute
}

class FakeRouteDetailsNavigator(desiredRoute: Call) extends RouteDetailsNavigator(Nil, Nil, Nil) {
  override def nextPage(userAnswers: UserAnswers, mode: Mode): Call = desiredRoute
}

class FakeRoutingNavigator(desiredRoute: Call) extends RoutingNavigator(Nil, Nil, Nil) {
  override def nextPage(userAnswers: UserAnswers, mode: Mode): Call = desiredRoute
}

class FakeCountryOfRoutingNavigator(desiredRoute: Call, index: Index) extends CountryOfRoutingNavigator(index, Nil, Nil, Nil) {
  override def nextPage(userAnswers: UserAnswers, mode: Mode): Call = desiredRoute
}

class FakeTransitNavigator(desiredRoute: Call) extends TransitNavigator(Nil, Nil, Nil) {
  override def nextPage(userAnswers: UserAnswers, mode: Mode): Call = desiredRoute
}

class FakeOfficeOfTransitNavigator(desiredRoute: Call, index: Index) extends OfficeOfTransitNavigator(index, Nil, Nil, Nil) {
  override def nextPage(userAnswers: UserAnswers, mode: Mode): Call = desiredRoute
}

class FakeOfficeOfExitNavigator(desiredRoute: Call, index: Index) extends OfficeOfExitNavigator(index, Nil, Nil, Nil) {
  override def nextPage(userAnswers: UserAnswers, mode: Mode): Call = desiredRoute
}

class FakeLocationOfGoodsNavigator(desiredRoute: Call) extends LocationOfGoodsNavigator(Nil, Nil, Nil) {
  override def nextPage(userAnswers: UserAnswers, mode: Mode): Call = desiredRoute
}
