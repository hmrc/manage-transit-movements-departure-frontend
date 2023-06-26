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

import connectors.CacheConnector
import controllers.actions._
import forms.NewLocalReferenceNumberFormProvider
import forms.preTaskList.LocalReferenceNumberFormProvider
import models.{LocalReferenceNumber, NormalMode, UserAnswers}
import navigation.PreTaskListNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.NewLocalReferenceNumberView
import views.html.preTaskList.LocalReferenceNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NewLocalReferenceNumberController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: PreTaskListNavigatorProvider,
  identify: IdentifierAction,
  formProvider: NewLocalReferenceNumberFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: NewLocalReferenceNumberView,
  cacheConnector: CacheConnector
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(alreadyExists: Boolean = false): Form[LocalReferenceNumber] = formProvider(alreadyExists)

  def onPageLoad(oldLocalReferenceNumber: LocalReferenceNumber): Action[AnyContent] = identify {
    implicit request =>
      Ok(view(form(), oldLocalReferenceNumber))
  }

  def isDuplicate(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[Option[Boolean]] =
    cacheConnector.isDuplicateLRN(lrn)

  def onSubmit(oldLocalReferenceNumber: LocalReferenceNumber, newLocalReferenceNumber: Option[LocalReferenceNumber]): Action[AnyContent] = identify.async {
    implicit request =>
      newLocalReferenceNumber match {
        case Some(newLocalReferenceNumber) =>
          isDuplicate(newLocalReferenceNumber) map {
            case None => Future.successful(Redirect(controllers.routes.ErrorController.internalServerError()))
            case Some(alreadyExists) =>
              form(alreadyExists)
                .bindFromRequest()
                .fold(
                  formWithErrors => Future.successful(BadRequest(view(formWithErrors, oldLocalReferenceNumber))),
                  value => {
                    val userAnswersToCopy = cacheConnector.get(oldLocalReferenceNumber)
                    userAnswersToCopy flatMap {
                      case Some(userAnswers) =>
                        val updatedUserAnswers = userAnswers.copy(lrn = value, eoriNumber = request.eoriNumber)
                        cacheConnector.post(updatedUserAnswers) map {
                          case true  => Future.successful(Redirect(controllers.routes.TaskListController.onPageLoad(value)))
                          case false => Future.successful(Redirect(controllers.routes.ErrorController.technicalDifficulties()))
                        }
                      case None => Future.successful(Redirect(controllers.routes.ErrorController.notFound()))
                    }
                  }
                )
          }
      }
  }
}
