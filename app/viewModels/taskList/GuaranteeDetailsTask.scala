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
import models.journeyDomain.guaranteeDetails.GuaranteeDetailsDomain
import pages.sections.GuaranteeDetailsSection
import play.api.libs.json.JsArray

case class GuaranteeDetailsTask(status: TaskStatus, href: Option[String]) extends Task {
  override val id: String         = "guarantee-details"
  override val messageKey: String = "guaranteeDetails"
}

object GuaranteeDetailsTask {

  def apply(userAnswers: UserAnswers): GuaranteeDetailsTask = {
    val (status, href) = new TaskProvider(userAnswers).noDependencyOnOtherTask
      .readUserAnswers[GuaranteeDetailsDomain, JsArray](
        section = GuaranteeDetailsSection,
        inProgressRoute = Some(controllers.guaranteeDetails.routes.AddAnotherGuaranteeController.onPageLoad(userAnswers.lrn))
      )

    new GuaranteeDetailsTask(status, href)
  }
}
