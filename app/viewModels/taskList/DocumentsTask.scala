/*
 * Copyright 2024 HM Revenue & Customs
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
import models.LocalReferenceNumber

case class DocumentsTask(status: TaskStatus) extends TaskListTask {
  override val id: String         = "documents"
  override val messageKey: String = "documents"
  override val section: String    = DocumentsTask.section

  override def href(lrn: LocalReferenceNumber)(implicit config: FrontendAppConfig): String =
    config.documentsFrontendUrl(lrn)
}

object DocumentsTask {
  val section: String = ".documents"
}
