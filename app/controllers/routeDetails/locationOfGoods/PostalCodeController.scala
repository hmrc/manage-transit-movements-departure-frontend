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

package controllers.routeDetails.locationOfGoods

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.locationOfGoods.PostalCodeFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.routeDetails.LocationOfGoodsNavigatorProvider
import pages.routeDetails.locationOfGoods.PostalCodePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.locationOfGoods.PostalCodeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PostalCodeController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: LocationOfGoodsNavigatorProvider,
  actions: Actions,
  formProvider: PostalCodeFormProvider,
  countriesService: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: PostalCodeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix: String = "routeDetails.locationOfGoods.postalCode"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .async {
      implicit request =>
        countriesService.getAddressPostcodeBasedCountries().map {
          countryList =>
            val form = formProvider(prefix, countryList)
            val preparedForm = request.userAnswers.get(PostalCodePage) match {
              case None        => form
              case Some(value) => form.fill(value)
            }

            Ok(view(preparedForm, lrn, mode, countryList.countries))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .async {
      implicit request =>
        countriesService.getAddressPostcodeBasedCountries().flatMap {
          countryList =>
            val form = formProvider(prefix, countryList)
            form
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, countryList.countries))),
                value =>
                  navigatorProvider(mode).flatMap {
                    implicit navigator =>
                      PostalCodePage.writeToUserAnswers(value).writeToSession().navigate()
                  }
              )

        }
    }
}
