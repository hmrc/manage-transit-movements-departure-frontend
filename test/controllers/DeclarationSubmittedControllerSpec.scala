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
import generators.Generators
import models.reference.CustomsOffice
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.external.OfficeOfDestinationPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.DeclarationSubmittedView

class DeclarationSubmittedControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private lazy val declarationSubmittedRoute: String = routes.DeclarationSubmittedController.onPageLoad(lrn).url

  "DeclarationSubmittedController" - {

    "must return OK and the correct view for a GET" in {
      forAll(arbitrary[CustomsOffice]) {
        officeOfDestination =>
          val userAnswers = emptyUserAnswers.setValue(OfficeOfDestinationPage, officeOfDestination)
          setExistingUserAnswers(userAnswers)

          val request = FakeRequest(GET, declarationSubmittedRoute)

          val result = route(app, request).value

          val view = injector.instanceOf[DeclarationSubmittedView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(lrn, officeOfDestination)(request, messages).toString
      }
    }

    "must redirect to Technical Difficulties Page for a GET if office of destination not found in user answers" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, declarationSubmittedRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.ErrorController.technicalDifficulties().url
    }

    "must redirect to Technical Difficulties Page for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, declarationSubmittedRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.ErrorController.technicalDifficulties().url
    }
  }
}
