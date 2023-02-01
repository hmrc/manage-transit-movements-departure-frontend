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

package controllers

import org.mockito.ArgumentMatchers.any
import base.{AppWithDefaultMockFixtures, SpecBase}
import models.DeclarationType.Option1
import models.ProcedureType.Normal
import models.SecurityDetailsType.NoSecurityDetails
import models.reference.CustomsOffice
import models.{LocalReferenceNumber, NormalMode}
import org.mockito.Mockito.{times, verify, when}
import pages.preTaskList.{DeclarationTypePage, DetailsConfirmedPage, OfficeOfDeparturePage, ProcedureTypePage, SecurityDetailsTypePage, TIRCarnetReferencePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import viewModels.taskList.TaskListViewModel

import scala.concurrent.Future

class DraftControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  "draft controller" - {
    "when the preTaskList is incomplete the next page will be a preTaskList page" in {

      setExistingUserAnswers(emptyUserAnswers.setValue(TIRCarnetReferencePage, ""))

      val request = FakeRequest(GET, routes.DraftController.draftRedirect(lrn).url)

      val result = route(app, request).value

      redirectLocation(result).value mustEqual controllers.preTaskList.routes.OfficeOfDepartureController.onPageLoad(lrn, NormalMode).url

    }

    "preTaskList is complete and the document is incomplete" in {
      setExistingUserAnswers(
        emptyUserAnswers
          .setValue(DeclarationTypePage, Option1)
          .setValue(OfficeOfDeparturePage, CustomsOffice("name", "phone", None))
          .setValue(ProcedureTypePage, Normal)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(DetailsConfirmedPage, true)
      )

      val request = FakeRequest(GET, routes.DraftController.draftRedirect(lrn).url)

      val result = route(app, request).value

      redirectLocation(result).value mustEqual controllers.routes.TaskListController.onPageLoad(lrn).url
    }

  }

}
