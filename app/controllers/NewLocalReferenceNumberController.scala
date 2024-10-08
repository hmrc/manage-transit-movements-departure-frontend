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

package controllers

import controllers.actions._
import forms.preTaskList.LocalReferenceNumberFormProvider
import models.LocalReferenceNumber
import play.api.data.{Form, FormError}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.DuplicateService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.NewLocalReferenceNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NewLocalReferenceNumberController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  formProvider: LocalReferenceNumberFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: NewLocalReferenceNumberView,
  duplicateService: DuplicateService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix: String = "newLocalReferenceNumber"

  private val form: Form[LocalReferenceNumber] = formProvider(prefix)

  def onPageLoad(oldLocalReferenceNumber: LocalReferenceNumber): Action[AnyContent] = identify.async {
    implicit request =>
      duplicateService.doesIE028ExistForLrn(oldLocalReferenceNumber).map {
        case true  => Ok(view(form, oldLocalReferenceNumber))
        case false => Redirect(controllers.routes.ErrorController.badRequest())
      }
  }

  def onSubmit(oldLocalReferenceNumber: LocalReferenceNumber): Action[AnyContent] = identify.async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, oldLocalReferenceNumber))),
          newLocalReferenceNumber =>
            duplicateService.doesDraftOrSubmissionExistForLrn(newLocalReferenceNumber).flatMap {
              case true =>
                val formWithErrors = form.withError(FormError("value", s"$prefix.error.alreadyExists"))
                Future.successful(BadRequest(view(formWithErrors, oldLocalReferenceNumber)))
              case false =>
                duplicateService.copyUserAnswers(oldLocalReferenceNumber, newLocalReferenceNumber) map {
                  case true  => Redirect(controllers.routes.TaskListController.onPageLoad(newLocalReferenceNumber))
                  case false => Redirect(controllers.routes.ErrorController.technicalDifficulties())
                }
            }
        )
  }
}
