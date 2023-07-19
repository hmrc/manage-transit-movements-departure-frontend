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

import base.{AppWithDefaultMockFixtures, SpecBase}
import models.DeclarationType.Option1
import models.NormalMode
import models.ProcedureType.Normal
import models.SecurityDetailsType.NoSecurityDetails
import models.reference.CustomsOffice
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import pages.preTaskList._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import services.DuplicateService

import scala.concurrent.Future

class DraftIndexControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val mockDuplicateService = mock[DuplicateService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super.guiceApplicationBuilder().overrides(bind[DuplicateService].toInstance(mockDuplicateService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDuplicateService)
  }

  "draft index controller" - {
    "when the submission does not exist" - {
      "and the preTaskList is incomplete the next page will be a preTaskList page" in {

        when(mockDuplicateService.doesSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(false))

        setExistingUserAnswers(emptyUserAnswers.setValue(TIRCarnetReferencePage, ""))

        val request = FakeRequest(GET, controllers.routes.DraftIndexController.index(lrn).url)

        val result = route(app, request).value

        redirectLocation(result).value mustEqual controllers.preTaskList.routes.OfficeOfDepartureController.onPageLoad(lrn, NormalMode).url

      }

      "and the preTaskList is complete and the document is incomplete" in {

        when(mockDuplicateService.doesSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(false))

        setExistingUserAnswers(
          emptyUserAnswers
            .setValue(DeclarationTypePage, Option1)
            .setValue(OfficeOfDeparturePage, CustomsOffice("name", "phone", None))
            .setValue(ProcedureTypePage, Normal)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(DetailsConfirmedPage, true)
        )

        val request = FakeRequest(GET, routes.DraftIndexController.index(lrn).url)

        val result = route(app, request).value

        redirectLocation(result).value mustEqual controllers.routes.TaskListController.onPageLoad(lrn).url
      }
    }

    "when the submission does exist" in {

      when(mockDuplicateService.doesSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(true))

      setExistingUserAnswers(
        emptyUserAnswers
          .setValue(DeclarationTypePage, Option1)
          .setValue(OfficeOfDeparturePage, CustomsOffice("name", "phone", None))
          .setValue(ProcedureTypePage, Normal)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(DetailsConfirmedPage, true)
      )

      val request = FakeRequest(GET, routes.DraftIndexController.index(lrn).url)

      val result = route(app, request).value

      redirectLocation(result).value mustEqual controllers.routes.DuplicateDraftLocalReferenceNumberController.onPageLoad().url
    }

  }

}
