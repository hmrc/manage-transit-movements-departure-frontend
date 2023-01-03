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

package navigation

import models.{Index, Mode, UserAnswers}
import navigation.routeDetails._
import navigation.traderDetails.TraderDetailsNavigator
import navigation.transport.{TransportMeansActiveNavigator, TransportMeansNavigator, TransportNavigator}
import play.api.mvc.Call

class FakeNavigator(desiredRoute: Call) extends Navigator {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakePreTaskListNavigator(desiredRoute: Call, mode: Mode) extends PreTaskListNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeGuaranteeNavigator(desiredRoute: Call, mode: Mode, index: Index) extends GuaranteeNavigator(mode, index) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeTraderDetailsNavigator(desiredRoute: Call, mode: Mode) extends TraderDetailsNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeRouteDetailsNavigator(desiredRoute: Call, mode: Mode) extends RouteDetailsNavigator(mode, Nil, Nil) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeRoutingNavigator(desiredRoute: Call, mode: Mode) extends RoutingNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeCountryOfRoutingNavigator(desiredRoute: Call, mode: Mode, index: Index) extends CountryOfRoutingNavigator(mode, index) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeTransitNavigator(desiredRoute: Call, mode: Mode) extends TransitNavigator(mode, Nil, Nil) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeOfficeOfTransitNavigator(desiredRoute: Call, mode: Mode, index: Index) extends OfficeOfTransitNavigator(mode, index, Nil, Nil) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeExitNavigator(desiredRoute: Call, mode: Mode) extends ExitNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeOfficeOfExitNavigator(desiredRoute: Call, mode: Mode, index: Index) extends OfficeOfExitNavigator(mode, index) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeLocationOfGoodsNavigator(desiredRoute: Call, mode: Mode) extends LocationOfGoodsNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeLoadingAndUnloadingNavigator(desiredRoute: Call, mode: Mode) extends LoadingAndUnloadingNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeTransportNavigator(desiredRoute: Call, mode: Mode) extends TransportNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeTransportMeansNavigator(desiredRoute: Call, mode: Mode) extends TransportMeansNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeTransportMeansActiveNavigator(desiredRoute: Call, mode: Mode, index: Index) extends TransportMeansActiveNavigator(mode, index) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}
