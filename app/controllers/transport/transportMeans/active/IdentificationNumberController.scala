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

package controllers.transport.transportMeans.active

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.transport.transportMeans.active.IdentificationNumberFormProvider
import models.journeyDomain.transport.TransportDomain
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.transport.TransportMeansActiveNavigatorProvider
import pages.transport.transportMeans.active.{IdentificationNumberPage, IdentificationPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.transportMeans.active.IdentificationNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IdentificationNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TransportMeansActiveNavigatorProvider,
  formProvider: IdentificationNumberFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: IdentificationNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix = "transport.transportMeans.active.identificationNumber"

  private def identificationType(index: Index)(implicit request: DataRequest[_]): Option[String] =
    IdentificationPage(index).inferredReader.run(request.userAnswers).toOption.map(_.forDisplay)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, activeIndex: Index): Action[AnyContent] =
    actions
      .requireData(lrn) {
        implicit request =>
          identificationType(activeIndex) match {
            case Some(value) =>
              val form = formProvider(prefix, value)

              val preparedForm = request.userAnswers.get(IdentificationNumberPage(activeIndex)) match {
                case None        => form
                case Some(value) => form.fill(value)
              }

              Ok(view(preparedForm, lrn, value, mode, activeIndex))
            case _ => Redirect(controllers.routes.SessionExpiredController.onPageLoad())
          }
      }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, activeIndex: Index): Action[AnyContent] =
    actions
      .requireData(lrn)
      .async {
        implicit request =>
          identificationType(activeIndex) match {
            case Some(value) =>
              val form = formProvider(prefix, value)
              form
                .bindFromRequest()
                .fold(
                  formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, value, mode, activeIndex))),
                  value => {
                    implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, activeIndex)
                    IdentificationNumberPage(activeIndex).writeToUserAnswers(value).updateTask[TransportDomain]().writeToSession().navigate()
                  }
                )
            case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
          }
      }
}
