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

package controllers.preTaskList

import controllers.actions._
import forms.preTaskList.LocalReferenceNumberFormProvider
import models.{LocalReferenceNumber, NormalMode, UserAnswers}
import navigation.PreTaskListNavigatorProvider
import play.api.data.Form
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
    with I18nSupport {

  private val prefix = "localReferenceNumber"

  private def form(alreadyExists: Boolean = false): Form[LocalReferenceNumber] = formProvider(alreadyExists, prefix)

  def onPageLoad(): Action[AnyContent] = identify {
    implicit request =>
      Ok(view(form()))
  }

  def onSubmit(): Action[AnyContent] = identify.async {
    implicit request =>
      val submittedValue = form().bindFromRequest().value
      duplicateService.alreadyExistsInSubmissionOrCache(submittedValue).flatMap {
        alreadyExists =>
          form(alreadyExists)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
              value => {
                def getOrCreateUserAnswers(): Future[Option[UserAnswers]] =
                  sessionRepository.get(value).flatMap {
                    case None =>
                      sessionRepository.put(value).flatMap {
                        _ => sessionRepository.get(value)
                      }
                    case someUserAnswers =>
                      Future.successful(someUserAnswers)
                  }

                getOrCreateUserAnswers().map {
                  case Some(userAnswers) => Redirect(navigatorProvider(NormalMode).nextPage(userAnswers))
                  case None              => Redirect(controllers.routes.ErrorController.technicalDifficulties())
                }
              }
            )
      }
  }
}
