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

package controllers.addItems.documents

import controllers.actions._
import forms.addItems.TIRCarnetReferenceFormProvider
import models.DeclarationType.Option4
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.addItems.AddItemsDocument
import pages.addItems.{DocumentTypePage, TIRCarnetReferencePage}
import pages.preTaskList.DeclarationTypePage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TirCarnetReferenceView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TIRCarnetReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @AddItemsDocument navigator: Navigator,
  actions: Actions,
  formProvider: TIRCarnetReferenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: TirCarnetReferenceView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index, documentIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(lrn) {
      implicit request =>
        val preparedForm = request.userAnswers.get(TIRCarnetReferencePage(itemIndex, documentIndex)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, lrn, mode, itemIndex, documentIndex))
    }

  def onSubmit(lrn: LocalReferenceNumber, itemIndex: Index, documentIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(lrn).async {
      implicit request =>
        request.userAnswers.get(DeclarationTypePage) match {
          case Some(Option4) =>
            form
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex, documentIndex))),
                value =>
                  for {
                    ua1 <- Future.fromTry(request.userAnswers.set(TIRCarnetReferencePage(documentIndex, itemIndex), value))
                    ua2 <- Future.fromTry(ua1.set(DocumentTypePage(Index(0), Index(0)), "952"))
                    _   <- sessionRepository.set(ua2)
                  } yield Redirect(navigator.nextPage(TIRCarnetReferencePage(documentIndex, itemIndex), mode, ua2))
              )
          case Some(otherOption) =>
            logger.warn(s"[Controller][TIRCarnetReference][onPageLoad] Cannot create TIR carnet reference for $otherOption")
            Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
          case None =>
            logger.warn(s"[Controller][TIRCarnetReference][onPageLoad] DeclarationTypePage is missing")
            Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
        }
    }
}
