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

package controllers.routeDetails.loading

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.PlaceOfLoadingLocationFormProvider
import models.requests.SpecificDataRequestProvider1
import models.{LocalReferenceNumber, Mode}
import navigation.routeDetails.LoadingNavigatorProvider
import pages.routeDetails.loading.PlaceOfLoadingLocationPage
import pages.routeDetails.locationOfGoods.contact.LocationOfGoodsContactNamePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.loading.PlaceOfLoadingLocationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PlaceOfLoadingLocationController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: LoadingNavigatorProvider,
  formProvider: PlaceOfLoadingLocationFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  view: PlaceOfLoadingLocationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(implicit request: Request): Form[String] =
    formProvider("routeDetails.loading.placeOfLoadingLocation", countryname)

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[_]

  private def countryname(implicit request: Request): String = request.arg

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(LocationOfGoodsContactNamePage)) { //todo change PlaceOfLoadingCountryPage once created
      implicit request =>
        val preparedForm = request.userAnswers.get(PlaceOfLoadingLocationPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Ok(view(preparedForm, lrn, countryname, mode))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(LocationOfGoodsContactNamePage)) //todo change to PlaceOfLoadingCountryPage once created
    .async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, countryname, mode))),
            value =>
              navigatorProvider().flatMap {
                implicit navigator =>
                  PlaceOfLoadingLocationPage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
              }
          )
    }
}
