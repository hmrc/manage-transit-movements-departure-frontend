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
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.AuthorisationReferenceNumberFormProvider
import models.ProcedureType.Simplified
import models.journeyDomain.transport.TransportDomain
import models.requests.DataRequest
import models.transport.authorisations.AuthorisationType
import models.transport.transportMeans.departure.InlandMode.{Air, Maritime, Rail}
import models.{Index, LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.transport.AuthorisationNavigatorProvider
import pages.preTaskList.ProcedureTypePage
import pages.traderDetails.consignment.ApprovedOperatorPage
import pages.transport.authorisationsAndLimit.authorisations.index.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import pages.transport.transportMeans.departure.InlandModePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.authorisationsAndLimit.authorisations.index.AuthorisationReferenceNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthorisationReferenceNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: AuthorisationNavigatorProvider,
  formProvider: AuthorisationReferenceNumberFormProvider,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AuthorisationReferenceNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix = "transport.authorisations.authorisationReferenceNumber"

  private type Request = DataRequest[AnyContent]

  private def authorisationType(authorisationIndex: Index)(implicit request: Request): Option[AuthorisationType] = {
    val reducedDataSet = ApprovedOperatorPage.inferredReader.run(request.userAnswers).toOption
    val inlandMode     = request.userAnswers.get(InlandModePage)
    val procedureType  = request.userAnswers.get(ProcedureTypePage)

    (reducedDataSet, inlandMode, procedureType) match {
      case (Some(true), Some(Maritime) | Some(Rail) | Some(Air), _) if authorisationIndex.isFirst => Some(AuthorisationType.TRD)
      case (Some(true), _, Some(Simplified)) if authorisationIndex.isFirst                        => Some(AuthorisationType.ACR)
      case _                                                                                      => request.userAnswers.get(AuthorisationTypePage(authorisationIndex))
    }
  }

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, authorisationIndex: Index): Action[AnyContent] = actions
    .requireData(lrn) {
      implicit request =>
        authorisationType(authorisationIndex) match {
          case Some(value) =>
            val dynamicTitle = s"$prefix.$value"
            val form         = formProvider(prefix, dynamicTitle)

            val preparedForm = request.userAnswers.get(AuthorisationReferenceNumberPage(authorisationIndex)) match {
              case None        => form
              case Some(value) => form.fill(value)
            }

            Ok(view(preparedForm, lrn, dynamicTitle, mode, authorisationIndex))
          case _ => Redirect(controllers.routes.SessionExpiredController.onPageLoad())
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, authorisationIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      authorisationType(authorisationIndex) match {
        case Some(value) =>
          val dynamicTitle = s"$prefix.$value"
          val form         = formProvider(prefix, dynamicTitle)

          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, dynamicTitle, mode, authorisationIndex))),
              value => {
                implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, authorisationIndex)
                AuthorisationReferenceNumberPage(authorisationIndex).writeToUserAnswers(value).updateTask[TransportDomain]().writeToSession().navigate()
              }
            )
        case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }
}
