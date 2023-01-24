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

package viewModels.taskList

import config.FrontendAppConfig
import models.UserAnswers

class TaskListViewModel {

  def apply(userAnswers: UserAnswers)(implicit config: FrontendAppConfig): Seq[Task] = {

    def task(section: String, href: String, dependentSections: Seq[String] = Nil): Option[Task] = {
      val tasks = userAnswers.tasks
      tasks.find(_.section == section).orElse {
        val status = if (dependentSections.allCompleted(tasks)) TaskStatus.NotStarted else TaskStatus.CannotStartYet
        Task(section, status, if (status == TaskStatus.CannotStartYet) None else Some(href))
      }
    }

    Seq(
      task(TraderDetailsTask.section, config.traderDetailsFrontendUrl(userAnswers.lrn)),
      task(RouteDetailsTask.section, config.routeDetailsFrontendUrl(userAnswers.lrn)),
      task(TransportTask.section, config.transportDetailsFrontendUrl(userAnswers.lrn), Seq(RouteDetailsTask.section, TransportTask.section)),
      task(GuaranteeDetailsTask.section, config.guaranteeDetailsFrontendUrl(userAnswers.lrn))
    ).flatten
  }
}
