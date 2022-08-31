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

package controllers.routeDetails.transit.index

import controllers.actions._
import controllers.routeDetails.transit.{routes => transitRoutes}
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.{Index, LocalReferenceNumber}
import pages.routeDetails.transit.index.OfficeOfTransitPage
import pages.sections.routeDetails.transit.OfficeOfTransitSection
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.transit.index.ConfirmRemoveOfficeOfTransitView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveOfficeOfTransitController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmRemoveOfficeOfTransitView,
  getMandatoryPage: SpecificDataRequiredActionProvider
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, index: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(OfficeOfTransitPage(index))) {
      implicit request =>
        val officeOfTransit = request.arg
        val form            = formProvider("routeDetails.transit.confirmRemoveOfficeOfTransit", officeOfTransit.name)
        Ok(view(form, lrn, index, officeOfTransit.name))
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(OfficeOfTransitPage(index)))
    .async {
      implicit request =>
        val officeOfTransit = request.arg
        val form            = formProvider("routeDetails.transit.confirmRemoveOfficeOfTransit", officeOfTransit.name)
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, index, officeOfTransit.name))),
            {
              case true =>
                OfficeOfTransitSection(index)
                  .removeFromUserAnswers()
                  .writeToSession()
                  .navigateTo(transitRoutes.AddAnotherOfficeOfTransitController.onPageLoad(lrn))
              case false =>
                Future.successful(Redirect(transitRoutes.AddAnotherOfficeOfTransitController.onPageLoad(lrn)))
            }
          )
    }
}
