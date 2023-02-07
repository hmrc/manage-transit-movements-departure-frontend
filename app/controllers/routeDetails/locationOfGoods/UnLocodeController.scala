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

package controllers.routeDetails.locationOfGoods

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.UnLocodeFormProvider
import models.journeyDomain.routeDetails.RouteDetailsDomain
import models.{LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.routeDetails.LocationOfGoodsNavigatorProvider
import pages.routeDetails.locationOfGoods.UnLocodePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.{CountriesService, UnLocodesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.locationOfGoods.UnLocodeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UnLocodeController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: LocationOfGoodsNavigatorProvider,
  actions: Actions,
  formProvider: UnLocodeFormProvider,
  unLocodesService: UnLocodesService,
  countriesService: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: UnLocodeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .async {
      implicit request =>
        unLocodesService.getUnLocodes().map {
          unLocodeList =>
            val form = formProvider("routeDetails.locationOfGoods.unLocode", unLocodeList)
            val preparedForm = request.userAnswers
              .get(UnLocodePage)
              .flatMap(
                x => unLocodeList.getUnLocode(x.unLocodeExtendedCode)
              )
              .map(form.fill)
              .getOrElse(form)
            Ok(view(preparedForm, lrn, unLocodeList.unLocodes, mode))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .async {
      implicit request =>
        unLocodesService.getUnLocodes().flatMap {
          unLocodeList =>
            val form = formProvider("routeDetails.locationOfGoods.unLocode", unLocodeList)
            form
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, unLocodeList.unLocodes, mode))),
                value =>
                  for {
                    ctcCountries                          <- countriesService.getCountryCodesCTC()
                    customsSecurityAgreementAreaCountries <- countriesService.getCustomsSecurityAgreementAreaCountries()
                    result <- {
                      implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, ctcCountries, customsSecurityAgreementAreaCountries)
                      UnLocodePage
                        .writeToUserAnswers(value)
                        .updateTask()(RouteDetailsDomain.userAnswersReader(ctcCountries.countryCodes, customsSecurityAgreementAreaCountries.countryCodes))
                        .writeToSession()
                        .navigate()
                    }
                  } yield result
              )
        }
    }
}
