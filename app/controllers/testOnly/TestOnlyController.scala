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

package controllers.testOnly

import connectors.testOnly.TestOnlyCacheConnector
import play.api.libs.Files.logger
import play.api.libs.json.{__, JsValue}
import play.api.mvc.{Action, DefaultActionBuilder, MessagesControllerComponents}
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, SessionId}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class TestOnlyController @Inject() (
  cc: MessagesControllerComponents,
  action: DefaultActionBuilder,
  connector: TestOnlyCacheConnector
)(implicit val ec: ExecutionContext)
    extends FrontendController(cc) {

  def setUserAnswers(sessionId: String): Action[JsValue] = action.async(parse.json) {
    implicit request =>
      implicit val headerCarrier: HeaderCarrier = hc
        .copy(authorization = request.headers.get("Authorization").map(Authorization.apply))
        .copy(sessionId = Some(SessionId(sessionId)))

      val json = request.body

      json.asOpt((__ \ "lrn").read[String]) match {
        case Some(lrn) =>
          (
            for {
              put <- connector.put(lrn)
              if put
              post <- connector.post(lrn, json)
              if post
            } yield Ok
          ).recover {
            case NonFatal(e) =>
              logger.warn(s"Internal Server error with message: ${e.getMessage}")
              InternalServerError

          }
        case None =>
          Future.successful(BadRequest("LRN missing from JSON"))
      }
  }
}
