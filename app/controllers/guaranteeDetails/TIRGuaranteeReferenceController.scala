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

package controllers.guaranteeDetails

import controllers.actions._
import forms.guaranteeDetails.TIRGuaranteeReferenceFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.GuaranteeDetails
import pages.guaranteeDetails
import pages.guaranteeDetails.TIRGuaranteeReferencePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TIRGuaranteeReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @GuaranteeDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: TIRGuaranteeReferenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form     = formProvider()
  private val template = "tirGuaranteeReference.njk"

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(guaranteeDetails.TIRGuaranteeReferencePage(index)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"  -> preparedForm,
        "index" -> index.display,
        "lrn"   -> lrn,
        "mode"  -> mode
      )

      renderer.render(template, json).map(Ok(_))
  }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"  -> formWithErrors,
              "index" -> index.display,
              "lrn"   -> lrn,
              "mode"  -> mode
            )

            renderer.render(template, json).map(BadRequest(_))
          },
          value =>
            for {
              userAnswers <- Future.fromTry(request.userAnswers.set(TIRGuaranteeReferencePage(index), value))
              _           <- sessionRepository.set(userAnswers)
            } yield Redirect(navigator.nextPage(TIRGuaranteeReferencePage(index), mode, userAnswers))
        )
  }
}
