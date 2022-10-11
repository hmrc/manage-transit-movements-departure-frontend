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

package controllers.actions

import models.requests.{DataRequest, NavigationRequest}
import navigation.UserAnswersNavigator
import play.api.mvc.ActionTransformer
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NavigatorActionProviderImpl @Inject() (implicit val executionContext: ExecutionContext) extends NavigatorActionProvider {

  override def apply[A](navigator: HeaderCarrier => Future[UserAnswersNavigator]): ActionTransformer[DataRequest, NavigationRequest] =
    new NavigatorAction(navigator)
}

trait NavigatorActionProvider {

  def apply[A](navigator: HeaderCarrier => Future[UserAnswersNavigator]): ActionTransformer[DataRequest, NavigationRequest]
}

class NavigatorAction(
  navigator: HeaderCarrier => Future[UserAnswersNavigator]
)(implicit val executionContext: ExecutionContext)
    extends ActionTransformer[DataRequest, NavigationRequest] {

  override protected def transform[A](request: DataRequest[A]): Future[NavigationRequest[A]] = {
    val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    navigator(hc).map(
      x => NavigationRequest(request = request, eoriNumber = request.eoriNumber, userAnswers = request.userAnswers, navigator = x)
    )
  }
}
