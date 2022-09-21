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

package controllers.routeDetails.loadingAndUnloading.unloading

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.LocationFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.routeDetails.LoadingAndUnloadingNavigatorProvider
import pages.routeDetails.loadingAndUnloading.unloading.CountryPage
import pages.routeDetails.loadingAndUnloading.unloading.LocationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.loadingAndUnloading.unloading.LocationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LocationController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: LoadingAndUnloadingNavigatorProvider,
  formProvider: LocationFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  view: LocationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(CountryPage)) {
      implicit request =>
        val location = request.arg.description
        val form     = formProvider("routeDetails.unloading.location", location)
        val preparedForm = request.userAnswers.get(LocationPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Ok(view(preparedForm, lrn, location, mode))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(CountryPage))
    .async {
      implicit request =>
        val location = request.arg.description
        val form     = formProvider("routeDetails.unloading.location", location)
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, location, mode))),
            value =>
              navigatorProvider().flatMap {
                implicit navigator =>
                  LocationPage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
              }
          )
    }
}
