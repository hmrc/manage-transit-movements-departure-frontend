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

package controllers.transport.preRequisites

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.CountryFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import pages.transport.preRequisites.ItemsDestinationCountryPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.preRequisites.ItemsDestinationCountryView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ItemsDestinationCountryController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @PreTaskListDetails implicit val navigator: Navigator,
  actions: Actions,
  formProvider: CountryFormProvider,
  service: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: ItemsDestinationCountryView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getCountries.map {
        countryList =>
          val form = formProvider("transport.preRequisites.itemsDestinationCountry", countryList)
          val preparedForm = request.userAnswers.get(ItemsDestinationCountryPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, lrn, countryList.countries, mode))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getCountries.flatMap {
        countryList =>
          val form = formProvider("transport.preRequisites.itemsDestinationCountry", countryList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, countryList.countries, mode))),
              value => ItemsDestinationCountryPage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
            )
      }
  }
}
