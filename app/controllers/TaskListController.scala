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

package controllers

import cats.implicits._
import com.google.inject.Inject
import controllers.actions.{Actions, CheckDependentTaskCompletedActionProvider}
import models.journeyDomain.{DepartureDomain, PreTaskListDomain}
import models.{domain, LocalReferenceNumber}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{ApiService, CountriesService}
import uk.gov.hmrc.http.HttpReads.{is2xx, is4xx}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.taskList.TaskListViewModel
import views.html.TaskListView

import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  checkDependentTaskCompleted: CheckDependentTaskCompletedActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: TaskListView,
  viewModel: TaskListViewModel,
  countriesService: CountriesService,
  apiService: ApiService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkDependentTaskCompleted[PreTaskListDomain])
    .async {
      implicit request =>
        for {
          ctcCountries                             <- countriesService.getCountryCodesCTC()
          customsSecurityAgreementAreaCountryCodes <- countriesService.getCustomsSecurityAgreementAreaCountries()
        } yield {
          val tasks = viewModel(request.userAnswers)(
            ctcCountries.countryCodes,
            customsSecurityAgreementAreaCountryCodes.countryCodes
          )
          Ok(view(lrn, tasks))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkDependentTaskCompleted[PreTaskListDomain])
    .async {
      implicit request =>
        // Either[ReaderError, A], TODO move to service layer

        val getData: Future[domain.EitherType[DepartureDomain]] = for {
          ctcCountries                          <- countriesService.getCountryCodesCTC()
          customsSecurityAgreementAreaCountries <- countriesService.getCustomsSecurityAgreementAreaCountries()
        } yield DepartureDomain
          .userAnswersReader(ctcCountries.countryCodes, customsSecurityAgreementAreaCountries.countryCodes)
          .run(request.userAnswers)

        // TODO another service layer
        val submit = getData.flatMap(
          _.traverse(apiService.submitDeclaration(_))
        )

        submit.flatMap {
          case Right(value) =>
            value.status match {
              case status if is2xx(status) =>
                Future.successful(Redirect(controllers.routes.DeclarationSubmittedController.onPageLoad()))
              case status if is4xx(status) => ???
              case _                       => ???
            }
          case Left(_) => ???
        }
    }

}
