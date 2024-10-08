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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import models.NormalMode
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.test.FakeRequest
import play.api.test.Helpers._

class DraftControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  "draft controller" - {
    "when the preTaskList is incomplete and isPreLodgeEnabled false the next page will be a standardDeclaration page" in {

      val app = super
        .guiceApplicationBuilder()
        .configure("features.isPreLodgeEnabled" -> false)
        .build()

      running(app) {
        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, routes.DraftController.draftRedirect(lrn).url)

        val result = route(app, request).value

        redirectLocation(result).value mustEqual controllers.preTaskList.routes.StandardDeclarationController.onPageLoad(lrn).url
      }

    }
    "when the preTaskList is incomplete isPreLodgeEnabled true the next page will be a preTaskList page" in {

      val app = super
        .guiceApplicationBuilder()
        .configure("features.isPreLodgeEnabled" -> true)
        .build()

      running(app) {
        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, routes.DraftController.draftRedirect(lrn).url)

        val result = route(app, request).value

        redirectLocation(result).value mustEqual controllers.preTaskList.routes.AdditionalDeclarationTypeController.onPageLoad(lrn, NormalMode).url
      }

    }

    "preTaskList is complete and the document is incomplete" in {
      forAll(arbitraryPreTaskListAnswers(emptyUserAnswers)) {
        userAnswers =>
          setExistingUserAnswers(userAnswers)

          val request = FakeRequest(GET, routes.DraftController.draftRedirect(lrn).url)

          val result = route(app, request).value

          redirectLocation(result).value mustEqual controllers.routes.TaskListController.onPageLoad(lrn).url
      }
    }

  }

}
