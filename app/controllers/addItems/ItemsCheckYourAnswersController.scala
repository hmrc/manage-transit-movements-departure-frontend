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

package controllers.addItems

import cats.data.NonEmptyList
import controllers.actions._
import models.journeyDomain.ItemSection
import models.{DependentSection, Index, LocalReferenceNumber, ValidateReaderLogger}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.{DocumentTypesService, PreviousDocumentTypesService, SpecialMentionTypesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.AddItemsCheckYourAnswersViewModel
import viewModels.sections.Section

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ItemsCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  specialMentionTypesService: SpecialMentionTypesService,
  previousDocumentTypesService: PreviousDocumentTypesService,
  documentTypesService: DocumentTypesService,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with ValidateReaderLogger
    with I18nSupport {

  private val template = "addItems/itemsCheckYourAnswers.njk"

  def onPageLoad(lrn: LocalReferenceNumber, index: Index): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.ItemDetails)).async {
      implicit request =>
        val buildJson: Future[JsObject] =
          for {
            previousReferencesDocumentTypes <- previousDocumentTypesService.getPreviousDocumentTypes()
            documentTypes                   <- documentTypesService.getDocumentTypes()
            specialMentions                 <- specialMentionTypesService.getSpecialMentionTypes()
          } yield {

            val sections: Seq[Section] =
              AddItemsCheckYourAnswersViewModel(
                request.userAnswers,
                index,
                documentTypes,
                previousReferencesDocumentTypes,
                specialMentions
              ).sections

            Json.obj(
              "lrn"         -> lrn,
              "sections"    -> Json.toJson(sections),
              "nextPageUrl" -> routes.AddAnotherItemController.onPageLoad(lrn).url
            )
          }

        ValidateReaderLogger[NonEmptyList[ItemSection]](request.userAnswers)

        buildJson.flatMap(renderer.render(template, _).map(Ok(_)))
    }
}
