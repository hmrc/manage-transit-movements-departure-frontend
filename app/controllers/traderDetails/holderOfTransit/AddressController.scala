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

package controllers.traderDetails.holderOfTransit

import controllers.actions._
import forms.AddressFormProvider
import models.requests.SpecificDataRequestProvider1
import models.{Address, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.TraderDetails
import pages.traderDetails.holderOfTransit.{AddressPage, NamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.traderDetails.holderOfTransit.AddressView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @TraderDetails navigator: Navigator,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: AddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddressView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[_]

  private def name(implicit request: Request): String = request.arg

  private def form(implicit request: Request): Form[Address] =
    formProvider("traderDetails.holderOfTransit.address", name)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(NamePage)) {
      implicit request =>
        val preparedForm = request.userAnswers.get(AddressPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, lrn, mode, name))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(NamePage))
    .async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, name))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AddressPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AddressPage, mode, updatedAnswers))
          )
    }
}
