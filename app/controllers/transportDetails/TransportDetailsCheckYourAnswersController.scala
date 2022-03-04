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

package controllers.transportDetails

import controllers.actions._
import controllers.{routes => mainRoutes}
import models.journeyDomain.TransportDetails
import models.{DependentSection, LocalReferenceNumber, ValidateReaderLogger}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.{CountriesService, TransportModesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.TransportDetailsCheckYourAnswersViewModel
import viewModels.sections.Section

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class TransportDetailsCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  val controllerComponents: MessagesControllerComponents,
  transportModesService: TransportModesService,
  countriesService: CountriesService,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with ValidateReaderLogger
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.TransportDetails)).async {
      implicit request =>
        countriesService.getCountries().flatMap {
          countryList =>
            transportModesService.getTransportModes().flatMap {
              transportModeList =>
                val sections: Seq[Section] = TransportDetailsCheckYourAnswersViewModel(request.userAnswers, countryList, transportModeList).sections
                val json = Json.obj(
                  "lrn"         -> lrn,
                  "sections"    -> Json.toJson(sections),
                  "nextPageUrl" -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url
                )

                ValidateReaderLogger[TransportDetails](request.userAnswers)

                renderer.render("transportDetailsCheckYourAnswers.njk", json).map(Ok(_))
            }
        }
    }
}
