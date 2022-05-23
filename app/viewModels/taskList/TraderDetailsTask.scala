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

package viewModels.taskList

import controllers.routes
import controllers.traderDetails.holderOfTransit.{routes => holderOfTransitRoutes}
import models.DeclarationType.Option4
import models.domain._
import models.journeyDomain.traderDetails._
import models.{NormalMode, UserAnswers}
import pages.preTaskList.DeclarationTypePage
import pages.traderDetails.holderOfTransit.EoriYesNoPage

case class TraderDetailsTask(status: TaskStatus, href: Option[String]) extends Task {
  override val id: String         = "trader-details"
  override val messageKey: String = "traderDetails"
}

object TraderDetailsTask {

  def apply(userAnswers: UserAnswers): TraderDetailsTask = {

    lazy val firstPageInJourney = userAnswers.get(DeclarationTypePage) match {
      case Some(Option4) => "#" // TODO - redirect to Transit Procedure TIR identification number
      case Some(_)       => holderOfTransitRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, NormalMode).url
      case None          => routes.SessionExpiredController.onPageLoad().url
    }

    new TaskProvider(userAnswers).noDependencyOnOtherTask
      .ifCompleted(
        readerIfCompleted = UserAnswersReader[TraderDetails],
        urlIfCompleted = "#" // TODO - trader details check your answers
      )
      .ifInProgressOrNotStarted(
        readerIfInProgress = EoriYesNoPage.reader, // TODO - .orElse(???), also check TransitProcedureTIRIdentificationNumberPage
        urlIfInProgressOrNotStarted = firstPageInJourney
      )
      .apply {
        new TraderDetailsTask(_, _)
      }
  }
}
