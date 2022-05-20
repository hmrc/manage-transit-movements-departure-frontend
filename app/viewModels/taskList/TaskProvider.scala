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

import cats.implicits._
import models.UserAnswers
import models.domain.UserAnswersReader
import viewModels.taskList.TaskStatus._

private[viewModels] class TaskProvider(userAnswers: UserAnswers) {

  def ifNoDependencyOnOtherTask[A, B]: DependentTaskStage[A] =
    new DependentTaskStage[A](userAnswers)(None)

  def ifDependentTaskCompleted[A, B](readerIfDependentTaskCompleted: UserAnswersReader[A]): DependentTaskStage[A] =
    new DependentTaskStage[A](userAnswers)(Some(readerIfDependentTaskCompleted))

  def conditionalDependencyOnTask[A, B](
    readerIfDependentTaskCompleted: UserAnswersReader[A]
  )(isDependent: Boolean): DependentTaskStage[A] =
    if (isDependent) {
      ifDependentTaskCompleted(readerIfDependentTaskCompleted)
    } else {
      ifNoDependencyOnOtherTask
    }

  class DependentTaskStage[A](userAnswers: UserAnswers)(
    readerIfDependentTaskCompleted: Option[UserAnswersReader[A]]
  ) {

    def ifCompleted[B](readerIfCompleted: UserAnswersReader[B], urlIfCompleted: String): IfCompletedStage[A, B] =
      new IfCompletedStage(userAnswers)(
        readerIfDependentTaskCompleted,
        readerIfCompleted,
        urlIfCompleted
      )
  }

  class IfCompletedStage[A, B](userAnswers: UserAnswers)(
    readerIfDependentTaskCompleted: Option[UserAnswersReader[A]],
    readerIfCompleted: UserAnswersReader[B],
    urlIfCompleted: String
  ) {

    def ifInProgress[C](readerIfInProgress: UserAnswersReader[C], urlIfInProgress: String): IfInProgressStage[A, B, C] =
      new IfInProgressStage(userAnswers)(
        readerIfDependentTaskCompleted,
        readerIfCompleted,
        urlIfCompleted,
        readerIfInProgress,
        urlIfInProgress
      )

    def ifInProgressOrNotStarted[C](readerIfInProgress: UserAnswersReader[C], urlIfInProgressOrNotStarted: String): ApplyStage[A, B, C] =
      new ApplyStage(userAnswers)(
        readerIfDependentTaskCompleted,
        readerIfCompleted,
        urlIfCompleted,
        readerIfInProgress,
        urlIfInProgressOrNotStarted,
        urlIfInProgressOrNotStarted
      )
  }

  class IfInProgressStage[A, B, C](userAnswers: UserAnswers)(
    readerIfDependentTaskCompleted: Option[UserAnswersReader[A]],
    readerIfCompleted: UserAnswersReader[B],
    urlIfCompleted: String,
    readerIfInProgress: UserAnswersReader[C],
    urlIfInProgress: String
  ) {

    def ifNotStarted(urlIfNotStarted: String): ApplyStage[A, B, C] =
      new ApplyStage(userAnswers)(
        readerIfDependentTaskCompleted,
        readerIfCompleted,
        urlIfCompleted,
        readerIfInProgress,
        urlIfInProgress,
        urlIfNotStarted
      )
  }

  class ApplyStage[A, B, C](userAnswers: UserAnswers)(
    readerIfDependentTaskCompleted: Option[UserAnswersReader[A]],
    readerIfCompleted: UserAnswersReader[B],
    urlIfCompleted: String,
    readerIfInProgress: UserAnswersReader[C],
    urlIfInProgress: String,
    urlIfNotStarted: String
  ) {

    def apply[T <: Task](f: (TaskStatus, Option[String]) => T): T = {
      lazy val completed = readerIfCompleted
        .map[(String, TaskStatus)](
          _ => (urlIfCompleted, Completed)
        )

      lazy val inProgress = readerIfInProgress
        .map[(String, TaskStatus)](
          _ => (urlIfInProgress, InProgress)
        )

      lazy val (onwardRoute, status) = completed
        .orElse(inProgress)
        .run(userAnswers)
        .getOrElse((urlIfNotStarted, NotStarted))

      val (updatedStatus, updatedOnwardRoute) = readerIfDependentTaskCompleted match {
        case Some(reader) =>
          reader.run(userAnswers) match {
            case Right(_) => (status, Some(onwardRoute))
            case _        => (CannotStartYet, None)
          }
        case _ => (status, Some(onwardRoute))
      }

      f(updatedStatus, updatedOnwardRoute)
    }
  }
}
