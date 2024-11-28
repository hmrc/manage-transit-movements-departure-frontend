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

package controllers.actions

import base.SpecBase
import generators.Generators
import models.UserAnswersResponse.Answers
import models.requests.{IdentifierRequest, OptionalDataRequest}
import models.{EoriNumber, LocalReferenceNumber, UserAnswersResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContent, Request, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository

import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with Generators {

  val sessionRepository: SessionRepository = mock[SessionRepository]

  override lazy val app: Application = {

    import play.api.inject._

    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionRepository].toInstance(sessionRepository)
      )
      .build()
  }

  def harness(lrn: LocalReferenceNumber, f: OptionalDataRequest[AnyContent] => Unit): Unit = {

    lazy val actionProvider = app.injector.instanceOf[DataRetrievalActionProviderImpl]

    actionProvider(lrn)
      .invokeBlock(
        IdentifierRequest(FakeRequest(GET, "/").asInstanceOf[Request[AnyContent]], EoriNumber("")),
        {
          (request: OptionalDataRequest[AnyContent]) =>
            f(request)
            Future.successful(Results.Ok)
        }
      )
      .futureValue
  }

  "a data retrieval action" - {

    "must return an OptionalDataRequest with an empty UserAnswers" - {

      "where there are no existing answers for this LRN" in {

        when(sessionRepository.get(any())(any())).thenReturn(Future.successful(UserAnswersResponse.NoAnswers))

        harness(lrn, request => request.userAnswers mustBe UserAnswersResponse.NoAnswers)
      }
    }

    "must return an OptionalDataRequest with some defined UserAnswers" - {

      "when there are existing answers for this LRN" in {

        when(sessionRepository.get(any())(any())).thenReturn(Future.successful(Answers(emptyUserAnswers)))

        harness(lrn, request => request.userAnswers mustBe Answers(emptyUserAnswers))
      }
    }
  }
}
