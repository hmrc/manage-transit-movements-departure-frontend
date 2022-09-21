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

package controllers.routeDetails.routing.index

import controllers.actions._
import controllers.routeDetails.routing.{routes => routingRoutes}
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.reference.Country
import models.requests.SpecificDataRequestProvider1
import models.{Index, LocalReferenceNumber, Mode}
import pages.routeDetails.routing.index.CountryOfRoutingPage
import pages.sections.routeDetails.routing.CountryOfRoutingSection
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.routing.index.RemoveCountryOfRoutingYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveCountryOfRoutingYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveCountryOfRoutingYesNoView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider1[Country]#SpecificDataRequest[_]

  private def form(implicit request: Request): Form[Boolean] =
    formProvider("routeDetails.routing.removeCountryOfRoutingYesNo", request.arg.toString)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(CountryOfRoutingPage(index))) {
      implicit request =>
        Ok(view(form, lrn, mode, index, request.arg))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(CountryOfRoutingPage(index)))
    .async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, index, request.arg))),
            {
              case true =>
                CountryOfRoutingSection(index)
                  .removeFromUserAnswers()
                  .writeToSession()
                  .navigateTo(routingRoutes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, mode))
              case false =>
                Future.successful(Redirect(routingRoutes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, mode)))
            }
          )
    }
}
