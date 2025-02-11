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
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl
import views.html.SessionExpiredView

class SessionExpiredControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  "Session Expired Controller" - {

    "must return OK and the correct view for a GET" - {

      "when LRN is defined" in {
        val request = FakeRequest(GET, routes.SessionExpiredController.onPageLoad(Some(lrn)).url)

        val result = route(app, request).value

        val view = injector.instanceOf[SessionExpiredView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(Some(lrn))(request, messages).toString
      }

      "when LRN is undefined" in {
        val request = FakeRequest(GET, routes.SessionExpiredController.onPageLoad(None).url)

        val result = route(app, request).value

        val view = injector.instanceOf[SessionExpiredView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(None)(request, messages).toString
      }
    }

    "must redirect to a new page for a POST" - {

      "when LRN is defined" in {
        val request =
          FakeRequest(POST, routes.SessionExpiredController.onSubmit(Some(lrn)).url)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          s"http://localhost:9949/auth-login-stub/gg-sign-in?continue=http%3A%2F%2Flocalhost%3A10120%2Fmanage-transit-movements%2Fdepartures"
      }

      "when LRN is undefined" in {
        val request =
          FakeRequest(POST, routes.SessionExpiredController.onSubmit(None).url)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          s"http://localhost:9949/auth-login-stub/gg-sign-in?continue=http%3A%2F%2Flocalhost%3A10120%2Fmanage-transit-movements%2Fdepartures"
      }
    }
  }
}
