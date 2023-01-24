/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.transport.authorisationsAndLimit.authorisations.index

import controllers.actions._
import controllers.transport.authorisationsAndLimit.authorisations.{routes => authRoutes}
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.journeyDomain.transport.TransportDomain
import models.requests.SpecificDataRequestProvider2
import models.transport.authorisations.AuthorisationType
import models.{Index, LocalReferenceNumber, Mode}
import pages.sections.transport.authorisationsAndLimit.AuthorisationSection
import pages.transport.authorisationsAndLimit.authorisations.index.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.authorisationsAndLimit.authorisations.index.RemoveAuthorisationYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveAuthorisationYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveAuthorisationYesNoView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider2[AuthorisationType, String]#SpecificDataRequest[_]

  private def authType(implicit request: Request): AuthorisationType = request.arg._1

  private def authRefNumber(implicit request: Request): String = request.arg._2

  private def form(implicit request: Request): Form[Boolean] =
    formProvider("transport.authorisations.index.removeAuthorisationYesNo", authType.forDisplay, authRefNumber)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, authorisationIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage.getFirst(AuthorisationTypePage(authorisationIndex)))
    .andThen(getMandatoryPage.getSecond(AuthorisationReferenceNumberPage(authorisationIndex))) {
      implicit request =>
        Ok(view(form, lrn, mode, authorisationIndex, authType.forDisplay, authRefNumber))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, authorisationIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage.getFirst(AuthorisationTypePage(authorisationIndex)))
    .andThen(getMandatoryPage.getSecond(AuthorisationReferenceNumberPage(authorisationIndex)))
    .async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, authorisationIndex, authType.forDisplay, authRefNumber))),
            {
              case true =>
                AuthorisationSection(authorisationIndex)
                  .removeFromUserAnswers()
                  .updateTask[TransportDomain]()
                  .writeToSession()
                  .navigateTo(authRoutes.AddAnotherAuthorisationController.onPageLoad(lrn, mode))
              case false =>
                Future.successful(Redirect(authRoutes.AddAnotherAuthorisationController.onPageLoad(lrn, mode)))
            }
          )
    }
}
