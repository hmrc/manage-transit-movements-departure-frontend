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
import forms.CurrencyCodeFormProvider
import models.journeyDomain.guaranteeDetails.GuaranteeDetailsDomain
import models.{Index, LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.guaranteeDetails.GuaranteeNavigatorProvider
import pages.guaranteeDetails.guarantee.CurrencyPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CurrenciesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.guaranteeDetails.guarantee.CurrencyView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CurrencyController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: GuaranteeNavigatorProvider,
  actions: Actions,
  formProvider: CurrencyCodeFormProvider,
  currenciesService: CurrenciesService,
  val controllerComponents: MessagesControllerComponents,
  view: CurrencyView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      currenciesService.getCurrencyCodes().map {
        currencyCodeList =>
          val form = formProvider("guaranteeDetails.guarantee.currency", currencyCodeList)
          val preparedForm = request.userAnswers.get(CurrencyPage(index)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, lrn, currencyCodeList.currencyCodes, mode, index))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      currenciesService.getCurrencyCodes().flatMap {
        currencyCodeList =>
          val form = formProvider("guaranteeDetails.guarantee.currency", currencyCodeList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, currencyCodeList.currencyCodes, mode, index))),
              value => {
                implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, index)
                CurrencyPage(index).writeToUserAnswers(value).updateTask[GuaranteeDetailsDomain]().writeToSession().navigate()
              }
            )
      }
  }
}
