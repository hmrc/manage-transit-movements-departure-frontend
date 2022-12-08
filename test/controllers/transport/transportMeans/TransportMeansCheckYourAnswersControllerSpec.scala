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

package controllers.transport.transportMeans

import base.{AppWithDefaultMockFixtures, SpecBase}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.transportMeans.TransportMeansCheckYourAnswersView

class TransportMeansCheckYourAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private lazy val transportMeansCheckYourAnswersRoute = routes.TransportMeansCheckYourAnswersController.onPageLoad(lrn).url

  "ActiveBorderCheckYourAnswers Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, transportMeansCheckYourAnswersRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[TransportMeansCheckYourAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn)(request, messages).toString
    }
  }
}
