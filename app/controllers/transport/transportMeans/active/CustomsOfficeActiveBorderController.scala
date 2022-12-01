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

package controllers.transport.transportMeans.active

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.CustomsOfficeFormProvider
import models.CustomsOfficeList.{officesOfExitReads, officesOfTransitReads}
import models.requests.DataRequest
import models.{CustomsOfficeList, Index, LocalReferenceNumber, Mode, RichOptionalJsArray}
import navigation.UserAnswersNavigator
import navigation.transport.TransportMeansNavigatorProvider
import pages.preTaskList.OfficeOfDeparturePage
import pages.sections.routeDetails.exit.OfficesOfExitSection
import pages.sections.routeDetails.transit.OfficesOfTransitSection
import pages.transport.transportMeans.active.CustomsOfficeActiveBorderPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.transportMeans.active.CustomsOfficeActiveBorderView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomsOfficeActiveBorderController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TransportMeansNavigatorProvider,
  actions: Actions,
  formProvider: CustomsOfficeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: CustomsOfficeActiveBorderView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, activeIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val customsOffices = getCustomsOffices
      val form           = formProvider("transport.transportMeans.active.customsOfficeActiveBorder", customsOffices)
      val preparedForm = request.userAnswers.get(CustomsOfficeActiveBorderPage(activeIndex)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, lrn, customsOffices, mode, activeIndex))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, activeIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val customsOffices = getCustomsOffices
      val form           = formProvider("transport.transportMeans.active.customsOfficeActiveBorder", customsOffices)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, customsOffices, mode, activeIndex))),
          value => {
            implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
            CustomsOfficeActiveBorderPage(activeIndex).writeToUserAnswers(value).writeToSession().navigate()
          }
        )
  }

  private def getCustomsOffices(implicit request: DataRequest[_]): CustomsOfficeList = {

    val officesOfExit     = request.userAnswers.get(OfficesOfExitSection).validate(officesOfExitReads).getCustomsOffices
    val officesOfTransit  = request.userAnswers.get(OfficesOfTransitSection).validate(officesOfTransitReads).getCustomsOffices
    val officeOfDeparture = request.userAnswers.get(OfficeOfDeparturePage).toList

    CustomsOfficeList(officesOfExit ++ officesOfTransit ++ officeOfDeparture)
  }
}
