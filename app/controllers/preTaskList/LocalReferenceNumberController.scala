/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.preTaskList

import controllers.actions.*
import forms.preTaskList.LocalReferenceNumberFormProvider
import models.{CheckMode, LocalReferenceNumber, NormalMode, SubmissionState}
import navigation.PreTaskListNavigatorProvider
import play.api.Logging
import play.api.data.{Form, FormError}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.DuplicateService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.preTaskList.LocalReferenceNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LocalReferenceNumberController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  duplicateService: DuplicateService,
  navigatorProvider: PreTaskListNavigatorProvider,
  identify: IdentifierAction,
  formProvider: LocalReferenceNumberFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: LocalReferenceNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private val prefix = "localReferenceNumber"

  private val form: Form[LocalReferenceNumber] = formProvider(prefix)

  def onPageLoad(): Action[AnyContent] = identify {
    implicit request =>
      Ok(view(form))
  }

  def onPageReload(lrn: LocalReferenceNumber): Action[AnyContent] = identify {
    implicit request =>
      Ok(view(form.fill(lrn)))
  }

  def onSubmit(): Action[AnyContent] = identify.async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
          lrn =>
            duplicateService.doesDraftOrSubmissionExistForLrn(lrn).flatMap {
              case true =>
                sessionRepository.get(lrn).map {
                  case Some(userAnswers) if userAnswers.status == SubmissionState.NotSubmitted =>
                    Redirect(navigatorProvider(CheckMode).nextPage(userAnswers, None))
                  case _ =>
                    val formWithErrors = form.withError(FormError("value", s"$prefix.error.alreadyExists"))
                    BadRequest(view(formWithErrors))
                }
              case false =>
                sessionRepository
                  .put(lrn)
                  .flatMap {
                    _ => sessionRepository.get(lrn)
                  }
                  .map {
                    case Some(userAnswers) => Redirect(navigatorProvider(NormalMode).nextPage(userAnswers, None))
                    case None              => Redirect(controllers.routes.ErrorController.technicalDifficulties())
                  }
            }
        )
  }
}
