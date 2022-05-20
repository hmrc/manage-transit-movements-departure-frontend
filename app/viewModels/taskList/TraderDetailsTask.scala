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

import controllers.routes._
import controllers.traderDetails.routes._
import models.DeclarationType.Option4
import models.domain.{GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.traderDetails.TraderDetails
import models.{NormalMode, UserAnswers}
import pages.preTaskList.DeclarationTypePage
import pages.traderDetails.TransitHolderEoriYesNoPage

case class TraderDetailsTask(status: TaskStatus, href: Option[String]) extends Task {
  override val id: String         = "trader-details"
  override val messageKey: String = "traderDetails"
}

object TraderDetailsTask {

  def apply(userAnswers: UserAnswers): TraderDetailsTask = {

    lazy val firstPageInJourney = userAnswers.get(DeclarationTypePage) match {
      case Some(Option4) => "#" // TODO - redirect to Transit Procedure TIR identification number
      case Some(_)       => TransitHolderEoriYesNoController.onPageLoad(userAnswers.lrn, NormalMode).url
      case None          => SessionExpiredController.onPageLoad().url
    }

    new TaskProvider(userAnswers).ifNoDependencyOnOtherTask
      .ifCompleted(
        readerIfCompleted = UserAnswersReader[TraderDetails],
        urlIfCompleted = "#" // TODO - trader details check your answers
      )
      .ifInProgressOrNotStarted(
        readerIfInProgress = TransitHolderEoriYesNoPage.reader, // TODO - also check TransitProcedureTIRIdentificationNumberPage
        urlIfInProgressOrNotStarted = firstPageInJourney
      )
      .apply {
        (status, href) => new TraderDetailsTask(status, href)
      }
  }
}
