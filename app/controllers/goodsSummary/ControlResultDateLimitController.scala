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

package controllers.goodsSummary

import controllers.actions._
import forms.ControlResultDateLimitFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.GoodsSummary
import pages.ControlResultDateLimitPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{DateInput, NunjucksSupport}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ControlResultDateLimitController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @GoodsSummary navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: ControlResultDateLimitFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def form: Form[LocalDate] = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(ControlResultDateLimitPage) match {
        case Some(value) => form.fill(value)
        case None        => form
      }

      val viewModel = DateInput.localDate(preparedForm("value"))

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode,
        "lrn"  -> lrn,
        "date" -> viewModel
      )

      renderer.render("controlResultDateLimit.njk", json).map(Ok(_))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val viewModel = DateInput.localDate(formWithErrors("value"))

            val json = Json.obj(
              "form" -> formWithErrors,
              "mode" -> mode,
              "lrn"  -> lrn,
              "date" -> viewModel
            )

            renderer.render("controlResultDateLimit.njk", json).map(BadRequest(_))
          },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ControlResultDateLimitPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(ControlResultDateLimitPage, mode, updatedAnswers))
        )
  }
}
