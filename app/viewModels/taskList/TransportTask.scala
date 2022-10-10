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

import models.UserAnswers
import models.journeyDomain.transport.TransportDomain
import pages.sections.transport.TransportSection
import play.api.libs.json.JsObject

case class TransportTask(status: TaskStatus, href: Option[String]) extends Task {
  override val id: String         = "transport-details"
  override val messageKey: String = "transportDetails"
}

object TransportTask {

  def apply(userAnswers: UserAnswers): TransportTask = {
    val (status, href) = new TaskProvider(userAnswers).noDependencyOnOtherTask // TODO - dependent on route details?
      .readUserAnswers[TransportDomain, JsObject](TransportSection)

    new TransportTask(status, href)
  }
}
