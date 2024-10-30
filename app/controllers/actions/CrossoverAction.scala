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

package controllers.actions

import com.google.inject.Inject
import config.PhaseConfig
import models.requests.DataRequest
import play.api.Logging
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}

import scala.concurrent.{ExecutionContext, Future}

class CrossoverActionProvider @Inject() (config: PhaseConfig)(implicit ec: ExecutionContext) {

  def apply(): ActionFilter[DataRequest] =
    new CrossoverAction(config)

}

class CrossoverAction(config: PhaseConfig)(implicit val executionContext: ExecutionContext) extends ActionFilter[DataRequest] with Logging {

  private val TransitionApiVersion = "2.0"
  private val isConfigTransitional = config.values.apiVersion.toString == TransitionApiVersion

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] =
    Future.successful {
      if isConfigTransitional != request.userAnswers.isTransitional then Some(Redirect(controllers.routes.DraftNoLongerAvailableController.onPageLoad()))
      else None
    }

}
