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

package controllers.transport.equipment.index.seal

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.ContainerSealIdentificationNumberFormProvider
import models.journeyDomain.transport.TransportDomain
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode, RichOptionalJsArray}
import navigation.UserAnswersNavigator
import navigation.transport.TransportNavigatorProvider
import pages.sections.SealsSection
import pages.transport.equipment.index.ContainerIdentificationNumberPage
import pages.transport.equipment.index.seal.ContainerSealIdentificationNumberPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.equipment.index.seal.ContainerSealIdentificationNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ContainerSealIdentificationNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TransportNavigatorProvider,
  formProvider: ContainerSealIdentificationNumberFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: ContainerSealIdentificationNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def prefix(equipmentIndex: Index)(implicit request: DataRequest[_]): (String, Seq[String]) =
    request.userAnswers
      .get(ContainerIdentificationNumberPage(equipmentIndex))
      .fold[(String, Seq[String])](("transport.equipment.index.seal.containerSealIdentificationNumber.withoutContainer", Seq.empty))(
        value => ("transport.equipment.index.seal.containerSealIdentificationNumber.withContainer", Seq(value))
      )

  private def form(prefix: String, args: Seq[String], equipmentIndex: Index, sealIndex: Index)(implicit
    request: DataRequest[_]
  ): Form[String] =
    formProvider(
      prefix,
      otherSealIdentificationNumbers(equipmentIndex, sealIndex),
      args: _*
    )

  private def otherSealIdentificationNumbers(equipmentIndex: Index, sealIndex: Index)(implicit request: DataRequest[_]): Seq[String] = {
    val numberOfSeals = request.userAnswers.get(SealsSection(equipmentIndex)).length
    (0 until numberOfSeals)
      .map(Index(_))
      .filterNot(_ == sealIndex)
      .map(ContainerSealIdentificationNumberPage(equipmentIndex, _))
      .flatMap(request.userAnswers.get(_))
  }

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, equipmentIndex: Index, sealIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val (p, args) = prefix(equipmentIndex)
      val preparedForm = request.userAnswers.get(ContainerSealIdentificationNumberPage(equipmentIndex: Index, sealIndex: Index)) match {
        case None        => form(p, args, equipmentIndex, sealIndex)
        case Some(value) => form(p, args, equipmentIndex, sealIndex).fill(value)
      }
      Ok(view(preparedForm, lrn, mode, equipmentIndex, sealIndex, p, args: _*))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, equipmentIndex: Index, sealIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val (p, args) = prefix(equipmentIndex)
      form(p, args, equipmentIndex, sealIndex)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, equipmentIndex, sealIndex, p, args: _*))),
          value => {
            implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
            ContainerSealIdentificationNumberPage(equipmentIndex: Index, sealIndex: Index)
              .writeToUserAnswers(value)
              .updateTask[TransportDomain]()
              .writeToSession()
              .navigate()
          }
        )
  }
}
