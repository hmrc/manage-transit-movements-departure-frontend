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

package controllers

import controllers.actions._
import forms.preTaskList.LocalReferenceNumberFormProvider
import models.SubmissionState.NotSubmitted
import models.{LocalReferenceNumber, NormalMode}
import navigation.PreTaskListNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.DuplicateService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DuplicateDraftLocalReferenceNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DuplicateDraftLocalReferenceNumberController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  duplicateService: DuplicateService,
  navigatorProvider: PreTaskListNavigatorProvider,
  identify: IdentifierAction,
  formProvider: LocalReferenceNumberFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DuplicateDraftLocalReferenceNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix = "duplicateDraftLocalReferenceNumber"

  private def form(alreadyExists: Boolean = false): Form[LocalReferenceNumber] = formProvider(alreadyExists, prefix)

  def onPageLoad(oldLocalReferenceNumber: LocalReferenceNumber): Action[AnyContent] = identify {
    implicit request =>
      Ok(view(form(), oldLocalReferenceNumber))
  }

  def onSubmit(oldLocalReferenceNumber: LocalReferenceNumber): Action[AnyContent] = identify.async {
    implicit request =>
      val submittedValue = form().bindFromRequest().value
      duplicateService.alreadySubmitted(submittedValue).flatMap {
        alreadyExists =>
          form(alreadyExists)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, oldLocalReferenceNumber))),
              newLocalReferenceNumber =>
                duplicateService.copyUserAnswers(oldLocalReferenceNumber, newLocalReferenceNumber, NotSubmitted).flatMap {
                  case true =>
                    handleRedirects(oldLocalReferenceNumber, newLocalReferenceNumber)
                  case false => Future.successful(Redirect(controllers.routes.ErrorController.technicalDifficulties()))
                }
            )
      }
  }

  private def handleRedirects(oldLocalReferenceNumber: LocalReferenceNumber, newLocalReferenceNumber: LocalReferenceNumber)(implicit
    hc: HeaderCarrier
  ): Future[Result] =
    sessionRepository.get(newLocalReferenceNumber).flatMap {
      case Some(userAnswers) =>
        sessionRepository.delete(oldLocalReferenceNumber).flatMap {
          case true  => Future.successful(Redirect(navigatorProvider(NormalMode).nextPage(userAnswers)))
          case false => Future.successful(Redirect(controllers.routes.ErrorController.technicalDifficulties()))
        }
      case None => Future.successful(Redirect(controllers.routes.ErrorController.technicalDifficulties()))
    }
}
