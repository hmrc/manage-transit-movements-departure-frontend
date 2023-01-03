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

package controllers.routeDetails.transit.index

import controllers.actions._
import controllers.routeDetails.transit.{routes => transitRoutes}
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.reference.CustomsOffice
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode}
import pages.routeDetails.transit.index.OfficeOfTransitPage
import pages.sections.routeDetails.transit.OfficeOfTransitSection
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.transit.index.ConfirmRemoveOfficeOfTransitView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveOfficeOfTransitController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmRemoveOfficeOfTransitView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private case class DynamicHeading(prefix: String, args: String*)

  private def dynamicHeading(index: Index)(implicit request: DataRequest[_]): Option[DynamicHeading] =
    request.userAnswers.get(OfficeOfTransitSection(index)) map {
      _ =>
        val prefix = "routeDetails.transit.index.confirmRemoveOfficeOfTransit"
        request.userAnswers.get(OfficeOfTransitPage(index)) match {
          case Some(CustomsOffice(_, name, _)) => DynamicHeading(prefix, name)
          case None                            => DynamicHeading(s"$prefix.default")
        }
    }

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      dynamicHeading(index) match {
        case Some(DynamicHeading(prefix, args @ _*)) =>
          Ok(view(formProvider(prefix, args: _*), lrn, mode, index, prefix, args: _*))
        case _ => Redirect(controllers.routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      dynamicHeading(index) match {
        case Some(DynamicHeading(prefix, args @ _*)) =>
          formProvider(prefix, args: _*)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, index, prefix, args: _*))),
              {
                case true =>
                  OfficeOfTransitSection(index)
                    .removeFromUserAnswers()
                    .writeToSession()
                    .navigateTo(transitRoutes.AddAnotherOfficeOfTransitController.onPageLoad(lrn, mode))
                case false =>
                  Future.successful(Redirect(transitRoutes.AddAnotherOfficeOfTransitController.onPageLoad(lrn, mode)))
              }
            )
        case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }
}
