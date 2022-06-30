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

package controllers.test

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.NameFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{TestNavigator, TestNavigatorProvider}
import pages.test.Test2Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.test.Test2View

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class Test2Controller @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TestNavigatorProvider,
  actions: Actions,
  formProvider: NameFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: Test2View
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("test.test2")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, fooIndex: Index, barIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(Test2Page(fooIndex, barIndex)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, lrn, mode, fooIndex, barIndex))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, fooIndex: Index, barIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, fooIndex, barIndex))),
          value => {
            implicit val navigator: TestNavigator = navigatorProvider(fooIndex, barIndex)
            Test2Page(fooIndex, barIndex).writeToUserAnswers(value).writeToSession().navigateWith(mode)
          }
        )
  }
}
