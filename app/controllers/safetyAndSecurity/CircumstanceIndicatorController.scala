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

package controllers.safetyAndSecurity

import controllers.actions._
import forms.safetyAndSecurity.CircumstanceIndicatorFormProvider
import models.reference.CircumstanceIndicator
import models.requests.DataRequest
import models.{CheckMode, CircumstanceIndicatorList, DependentSection, LocalReferenceNumber, Mode, UserAnswers}
import navigation.Navigator
import navigation.annotations.SafetyAndSecurity
import pages.QuestionPage
import pages.safetyAndSecurity.{AddPlaceOfUnloadingCodePage, CircumstanceIndicatorPage, PlaceOfUnloadingCodePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc._
import play.twirl.api.Html
import renderer.Renderer
import repositories.SessionRepository
import services.CircumstanceIndicatorsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.getCircumstanceIndicatorsAsJson

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CircumstanceIndicatorController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SafetyAndSecurity navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  formProvider: CircumstanceIndicatorFormProvider,
  circumstanceIndicatorsService: CircumstanceIndicatorsService,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "safetyAndSecurity/circumstanceIndicator.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        circumstanceIndicatorsService.getCircumstanceIndicators() flatMap {
          indicators =>
            val form = formProvider(indicators)

            val preparedForm = request.userAnswers
              .get(CircumstanceIndicatorPage)
              .flatMap(indicators.getCircumstanceIndicator)
              .map(form.fill)
              .getOrElse(form)

            renderPage(lrn, mode, preparedForm, indicators).map(Ok(_))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        circumstanceIndicatorsService.getCircumstanceIndicators() flatMap {
          indicatorList =>
            formProvider(indicatorList)
              .bindFromRequest()
              .fold(
                formWithErrors => renderPage(lrn, mode, formWithErrors, indicatorList).map(BadRequest(_)),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(CircumstanceIndicatorPage, value.code))
                    _              <- sessionRepository.set(updatedAnswers)
                    result         <- redirectRoutesF(value.code, mode, updatedAnswers)
                  } yield result
              )
        }
    }

  private def renderPage(lrn: LocalReferenceNumber, mode: Mode, form: Form[CircumstanceIndicator], circumstanceIndicatorList: CircumstanceIndicatorList)(
    implicit request: Request[AnyContent]
  ): Future[Html] = {
    val json = Json.obj(
      "form"                   -> form,
      "lrn"                    -> lrn,
      "mode"                   -> mode,
      "circumstanceIndicators" -> getCircumstanceIndicatorsAsJson(form.value, circumstanceIndicatorList.circumstanceIndicators)
    )

    renderer.render(template, json)
  }

  private def updateUserAnswers[A](page: QuestionPage[A], value: A, userAnswers: UserAnswers)(implicit
    writes: Writes[A],
    reads: Reads[A]
  ): Future[UserAnswers] =
    for {
      updatedAnswers <- Future.fromTry(userAnswers.set(page, value))
      _              <- sessionRepository.set(updatedAnswers)
    } yield updatedAnswers

  private def redirectRoutesF(circumstanceIndicatorCode: String, mode: Mode, userAnswers: UserAnswers)(implicit
    request: DataRequest[AnyContent]
  ): Future[Result] =
    if (mode == CheckMode) {
      (circumstanceIndicatorCode, request.userAnswers.get(PlaceOfUnloadingCodePage)) match {
        case ("E", Some(_)) =>
          updateUserAnswers(AddPlaceOfUnloadingCodePage, true, userAnswers).map(
            updatedAnswers => Redirect(navigator.nextPage(CircumstanceIndicatorPage, mode, updatedAnswers))
          )
        case ("E", None)  => Future.successful(Redirect(navigator.nextPage(CircumstanceIndicatorPage, mode, userAnswers)))
        case (_, Some(_)) => Future.successful(Redirect(navigator.nextPage(CircumstanceIndicatorPage, mode, userAnswers)))
        case (_, None) =>
          updateUserAnswers(AddPlaceOfUnloadingCodePage, true, userAnswers).map(
            updatedAnswers => Redirect(navigator.nextPage(AddPlaceOfUnloadingCodePage, mode, updatedAnswers))
          )
        case _ => Future.successful(Redirect(navigator.nextPage(CircumstanceIndicatorPage, mode, userAnswers)))
      }
    } else {
      Future.successful(Redirect(navigator.nextPage(CircumstanceIndicatorPage, mode, userAnswers)))
    }

}
