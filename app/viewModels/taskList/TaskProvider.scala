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

import models.domain.UserAnswersReader
import models.journeyDomain.Stage.AccessingJourney
import models.journeyDomain.{JourneyDomainModel, ReaderError}
import models.{CheckMode, NormalMode, UserAnswers}
import pages.sections.Section
import play.api.libs.json.{JsValue, Reads}
import play.api.mvc.Call
import viewModels.taskList.TaskStatus._

private[viewModels] class TaskProvider(userAnswers: UserAnswers) {

  def noDependencyOnOtherTask[A, B]: DependentTaskStage[A] =
    new DependentTaskStage[A](userAnswers)(None)

  def ifDependentTaskCompleted[A, B](readerIfDependentTaskCompleted: UserAnswersReader[A]): DependentTaskStage[A] =
    new DependentTaskStage[A](userAnswers)(Some(readerIfDependentTaskCompleted))

  def conditionalDependencyOnTask[A, B](
    readerIfDependentTaskCompleted: UserAnswersReader[A]
  )(isDependent: Boolean): DependentTaskStage[A] =
    if (isDependent) {
      ifDependentTaskCompleted(readerIfDependentTaskCompleted)
    } else {
      noDependencyOnOtherTask
    }

  class DependentTaskStage[A](userAnswers: UserAnswers)(
    readerIfDependentTaskCompleted: Option[UserAnswersReader[A]]
  ) {

    def readUserAnswers[T <: JourneyDomainModel, U <: JsValue](
      section: Section[U],
      inProgressRoute: Option[Call] = None
    )(implicit rds: Reads[U], userAnswersReader: UserAnswersReader[T]): (TaskStatus, Option[String]) = {
      lazy val (status, onwardRoute) = userAnswersReader.run(userAnswers) match {
        case Left(ReaderError(page, _)) =>
          val route = page.route(userAnswers, NormalMode).map(_.url)
          userAnswers.get(section) match {
            case Some(_) => (InProgress, inProgressRoute.map(_.url).orElse(route))
            case None    => (NotStarted, route)
          }
        case Right(value) => (Completed, value.routeIfCompleted(userAnswers, CheckMode, AccessingJourney).map(_.url))
      }

      readerIfDependentTaskCompleted match {
        case Some(reader) =>
          reader.run(userAnswers) match {
            case Right(_) => (status, onwardRoute)
            case _        => (CannotStartYet, None)
          }
        case _ => (status, onwardRoute)
      }
    }
  }
}
