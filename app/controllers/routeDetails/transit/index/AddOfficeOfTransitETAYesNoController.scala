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
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.routeDetails.OfficeOfTransitNavigatorProvider
import pages.routeDetails.transit.index.{AddOfficeOfTransitETAYesNoPage, OfficeOfTransitPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.transit.index.AddOfficeOfTransitETAYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddOfficeOfTransitETAYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: OfficeOfTransitNavigatorProvider,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddOfficeOfTransitETAYesNoView,
  getMandatoryPage: SpecificDataRequiredActionProvider
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] =
    actions
      .requireData(lrn)
      .andThen(getMandatoryPage(OfficeOfTransitPage(index))) {
        implicit request =>
          val officeOfTransit = request.arg
          val form            = formProvider("routeDetails.transit.index.addOfficeOfTransitETAYesNo")
          val preparedForm = request.userAnswers.get(AddOfficeOfTransitETAYesNoPage(index)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }
          Ok(view(preparedForm, lrn, mode, officeOfTransit, index))
      }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] =
    actions
      .requireData(lrn)
      .andThen(getMandatoryPage(OfficeOfTransitPage(index)))
      .async {
        implicit request =>
          val officeOfTransit = request.arg
          val form            = formProvider("routeDetails.transit.index.addOfficeOfTransitETAYesNo")
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, officeOfTransit, index))),
              value =>
                navigatorProvider(mode, index).flatMap {
                  implicit navigator =>
                    AddOfficeOfTransitETAYesNoPage(index).writeToUserAnswers(value).writeToSession().navigate()
                }
            )
      }
}
