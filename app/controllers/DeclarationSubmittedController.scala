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

package controllers

import connectors.CacheConnector
import controllers.actions.{Actions, SpecificDataRequiredActionProvider}
import models.LocalReferenceNumber
import pages.external.OfficeOfDestinationPage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.DeclarationSubmittedView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DeclarationSubmittedController @Inject() (
  cc: MessagesControllerComponents,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  view: DeclarationSubmittedView,
  connector: CacheConnector
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport
    with Logging {

  def departureDeclarationSubmitted(lrn: LocalReferenceNumber): Action[AnyContent] =
    onPageLoad(lrn, "IE015")

  def departureAmendmentSubmitted(lrn: LocalReferenceNumber): Action[AnyContent] =
    onPageLoad(lrn, "IE013")

  private def onPageLoad(lrn: LocalReferenceNumber, messageType: String): Action[AnyContent] = actions
    .requireDataIgnoreLock(lrn)
    .andThen(getMandatoryPage(OfficeOfDestinationPage))
    .async {
      implicit request =>
        connector.getMessages(lrn).map {
          messages =>
            if (messages.contains(messageType)) {
              Ok(view(lrn, request.arg))
            } else {
              logger.warn(s"$messageType not found for LRN $lrn")
              InternalServerError
            }
        }
    }
}
