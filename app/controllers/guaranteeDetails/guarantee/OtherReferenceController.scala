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

package controllers.guaranteeDetails.guarantee

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.OtherReferenceFormProvider
import models.GuaranteeType.{CashDepositGuarantee, GuaranteeNotRequiredExemptPublicBody}
import models.requests.SpecificDataRequestProvider1
import models.{GuaranteeType, Index, LocalReferenceNumber, Mode}
import navigation.{GuaranteeNavigatorProvider, UserAnswersNavigator}
import pages.guaranteeDetails.guarantee.{GuaranteeTypePage, OtherReferencePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.guaranteeDetails.guarantee.OtherReferenceView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OtherReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: GuaranteeNavigatorProvider,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: OtherReferenceFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: OtherReferenceView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider1[GuaranteeType]#SpecificDataRequest[_]

  private def form(prefix: String): Form[String] = formProvider(prefix)

  private def getValidPrefixOrRedirect(implicit request: Request): Either[(LocalReferenceNumber, Mode, Index) => Result, String] =
    request.arg match {
      case CashDepositGuarantee =>
        Right("guaranteeDetails.guarantee.otherReference.option3")
      case GuaranteeNotRequiredExemptPublicBody =>
        Right("guaranteeDetails.guarantee.otherReference.option8")
      case _ =>
        Left(
          (lrn, mode, index) => Redirect(routes.GuaranteeTypeController.onPageLoad(lrn, mode, index))
        )
    }

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(GuaranteeTypePage(index))) {
      implicit request =>
        getValidPrefixOrRedirect match {
          case Left(redirect) => redirect(lrn, mode, index)
          case Right(prefix) =>
            val preparedForm = request.userAnswers.get(OtherReferencePage(index)) match {
              case None        => form(prefix)
              case Some(value) => form(prefix).fill(value)
            }
            Ok(view(preparedForm, lrn, mode, index, prefix))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(GuaranteeTypePage(index)))
    .async {
      implicit request =>
        getValidPrefixOrRedirect match {
          case Left(redirect) => Future.successful(redirect(lrn, mode, index))
          case Right(prefix) =>
            form(prefix)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, index, prefix))),
                value => {
                  implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, index)
                  OtherReferencePage(index).writeToUserAnswers(value).writeToSession().navigate()
                }
              )
        }
    }
}
