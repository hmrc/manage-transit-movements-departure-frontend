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

package controllers.routeDetails.locationOfGoods.contact

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.ContactTelephoneNumberFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.routeDetails.LocationOfGoodsNavigatorProvider
import pages.routeDetails.locationOfGoods.contact.{LocationOfGoodsContactNamePage, TelephoneNumberPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.locationOfGoods.contact.ContactTelephoneNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TelephoneNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: LocationOfGoodsNavigatorProvider,
  formProvider: ContactTelephoneNumberFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  view: ContactTelephoneNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(LocationOfGoodsContactNamePage)) {
      implicit request =>
        val contactName = request.arg
        val form        = formProvider("routeDetails.locationOfGoods.contact.telephoneNumber", contactName)
        val preparedForm = request.userAnswers.get(TelephoneNumberPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Ok(view(preparedForm, lrn, contactName, mode))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(LocationOfGoodsContactNamePage))
    .async {
      implicit request =>
        val contactName = request.arg
        val form        = formProvider("routeDetails.locationOfGoods.contact.telephoneNumber", contactName)
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, contactName, mode))),
            value =>
              navigatorProvider().flatMap {
                implicit navigator =>
                  TelephoneNumberPage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
              }
          )
    }
}
