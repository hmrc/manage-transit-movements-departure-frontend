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

package controllers.actions

import base.{AppWithDefaultMockFixtures, SpecBase}
import com.google.inject.Inject
import connectors.EnrolmentStoreConnector
import controllers.actions.AuthActionSpec._
import controllers.routes
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, when}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{~, Retrieval}
import uk.gov.hmrc.auth.{core => authClient}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase with AppWithDefaultMockFixtures {

  class Harness(authAction: IdentifierAction) {

    def onPageLoad(): Action[AnyContent] = authAction {
      _ =>
        Results.Ok
    }
  }

  val mockAuthConnector: AuthConnector                     = mock[AuthConnector]
  val mockEnrolmentStoreConnector: EnrolmentStoreConnector = mock[EnrolmentStoreConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[EnrolmentStoreConnector].toInstance(mockEnrolmentStoreConnector))
      .configure("allowlist.enabled" -> false)

  val ENROLMENT_KEY    = "HMRC-CTC-ORG"
  val ENROLMENT_ID_KEY = "EORINumber"

  private def createEnrolment(key: String, identifierKey: Option[String], id: String, state: String) =
    Enrolment(
      key = key,
      identifiers = identifierKey match {
        case Some(idKey) => Seq(EnrolmentIdentifier(idKey, id))
        case None        => Seq.empty
      },
      state = state
    )

  "Auth Action" - {

    "when the user hasn't logged in" - {
      "must redirect the user to log in " in {
        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(new FakeFailingAuthConnector(new MissingBearerToken)))
          .build()

        running(app) {
          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER

          redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
        }
      }
    }

    "when the user's session has expired" - {
      "must redirect the user to log in " in {
        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(new FakeFailingAuthConnector(new BearerTokenExpired)))
          .build()

        running(app) {
          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER

          redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
        }
      }
    }

    "when the user doesn't have sufficient enrolments" - {
      "must redirect the user to the unauthorised page" in {
        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(new FakeFailingAuthConnector(new InsufficientEnrolments)))
          .build()

        running(app) {
          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER

          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
        }
      }
    }

    "when the user doesn't have sufficient confidence level" - {
      "must redirect the user to the unauthorised page" in {
        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(new FakeFailingAuthConnector(new InsufficientConfidenceLevel)))
          .build()

        running(app) {
          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER

          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
        }
      }
    }

    "when the user used an unaccepted auth provider" - {
      "must redirect the user to the unauthorised page" in {
        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(new FakeFailingAuthConnector(new UnsupportedAuthProvider)))
          .build()

        running(app) {
          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER

          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
        }
      }
    }

    "when the user has an unsupported affinity group" - {
      "must redirect the user to the unauthorised page" in {
        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(new FakeFailingAuthConnector(new UnsupportedAffinityGroup)))
          .build()

        running(app) {
          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER

          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
        }
      }
    }

    "when the user has an unsupported credential role" - {
      "must redirect the user to the unauthorised page" in {
        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(new FakeFailingAuthConnector(new UnsupportedCredentialRole)))
          .build()

        running(app) {
          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER

          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
        }
      }
    }

    "when given new enrolments without eori" - {
      "must redirect to unauthorised page" in {
        val newEnrolmentsWithoutEori: Enrolments = Enrolments(
          Set(
            createEnrolment("IR-SA", Some("UTR"), "123", "Activated"),
            createEnrolment(ENROLMENT_KEY, None, "999", "Activated"),
            createEnrolment("IR-CT", Some("UTR"), "456", "Activated")
          )
        )

        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(app) {
          when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
            .thenReturn(Future.successful(newEnrolmentsWithoutEori ~ Some("testName")))

          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER

          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
        }
      }
    }

    "when given user has no active new enrolments but new group has" - {
      "must redirect to unauthorised page with group access" in {
        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(app) {
          when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
            .thenReturn(Future.successful(Enrolments(Set.empty) ~ Some("testName")))
          when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), eqTo(ENROLMENT_KEY))(any()))
            .thenReturn(Future.successful(true))

          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER

          redirectLocation(result).value mustBe controllers.routes.UnauthorisedWithGroupAccessController.onPageLoad().url
        }
      }
    }

    "when given user has no enrolments but group has" - {
      "must redirect to unauthorised page with group access" in {
        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(app) {
          when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
            .thenReturn(Future.successful(Enrolments(Set.empty) ~ Some("testName")))
          when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), eqTo(ENROLMENT_KEY))(any())).thenReturn(Future.successful(true))

          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER

          redirectLocation(result).value mustBe controllers.routes.UnauthorisedWithGroupAccessController.onPageLoad().url
        }
      }
    }

    "when given both user and group has no enrolments" - {
      "must redirect to unauthorised page without group access" in {
        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(app) {
          when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
            .thenReturn(Future.successful(Enrolments(Set.empty) ~ Some("testName")))
          when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), eqTo(ENROLMENT_KEY))(any())).thenReturn(Future.successful(false))

          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(frontendAppConfig.eccEnrolmentSplashPage)
        }
      }
    }

    "when given user has no enrolments and there is no group" - {
      "must redirect to unauthorised page without group access" in {
        val app = guiceApplicationBuilder()
          .overrides(bind[AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(app) {
          when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
            .thenReturn(Future.successful(Enrolments(Set.empty) ~ None))

          when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), eqTo(ENROLMENT_KEY))(any())).thenReturn(Future.successful(false))

          val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(authAction)
          val result     = controller.onPageLoad()(fakeRequest)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(frontendAppConfig.eccEnrolmentSplashPage)
        }
      }
    }

    "when given new enrolments with eori" - {
      "must return Ok" - {
        "when EORI is allowed" - {
          "when exact EORI" in {
            val newEnrolmentsWithEori: Enrolments = Enrolments(
              Set(
                createEnrolment("IR-SA", Some("UTR"), "123", "Activated"),
                createEnrolment(ENROLMENT_KEY, Some(ENROLMENT_ID_KEY), "123", "NotYetActivated"),
                createEnrolment(ENROLMENT_KEY, Some(ENROLMENT_ID_KEY), "456", "Activated")
              )
            )

            val app = guiceApplicationBuilder()
              .overrides(bind[AuthConnector].toInstance(mockAuthConnector))
              .configure(
                "allowlist.enabled" -> true,
                "allowlist.eoris.0" -> "^456$"
              )
              .build()

            running(app) {
              when(mockAuthConnector.authorise[Enrolments ~ Some[String]](any(), any())(any(), any()))
                .thenReturn(Future.successful(newEnrolmentsWithEori ~ Some("testName")))

              val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
              val controller = new Harness(authAction)
              val result     = controller.onPageLoad()(fakeRequest)

              status(result) mustBe OK
            }
          }

          "when EORI matches" in {
            val newEnrolmentsWithEori: Enrolments = Enrolments(
              Set(
                createEnrolment(ENROLMENT_KEY, Some(ENROLMENT_ID_KEY), "XI123456", "Activated")
              )
            )

            val app = guiceApplicationBuilder()
              .overrides(bind[AuthConnector].toInstance(mockAuthConnector))
              .configure(
                "allowlist.enabled" -> true,
                "allowlist.eoris.0" -> "^XI(.+)$"
              )
              .build()

            running(app) {
              when(mockAuthConnector.authorise[Enrolments ~ Some[String]](any(), any())(any(), any()))
                .thenReturn(Future.successful(newEnrolmentsWithEori ~ Some("testName")))

              val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
              val controller = new Harness(authAction)
              val result     = controller.onPageLoad()(fakeRequest)

              status(result) mustBe OK
            }
          }
        }
      }

      "must return Service Unavailable" - {
        "when EORI is not allowed" in {
          val newEnrolmentsWithEori: Enrolments = Enrolments(
            Set(
              createEnrolment("IR-SA", Some("UTR"), "123", "Activated"),
              createEnrolment(ENROLMENT_KEY, Some(ENROLMENT_ID_KEY), "123", "NotYetActivated"),
              createEnrolment(ENROLMENT_KEY, Some(ENROLMENT_ID_KEY), "456", "Activated")
            )
          )

          val app = guiceApplicationBuilder()
            .overrides(bind[AuthConnector].toInstance(mockAuthConnector))
            .configure(
              "allowlist.enabled" -> true,
              "allowlist.eoris.0" -> "789"
            )
            .build()

          running(app) {
            when(mockAuthConnector.authorise[Enrolments ~ Some[String]](any(), any())(any(), any()))
              .thenReturn(Future.successful(newEnrolmentsWithEori ~ Some("testName")))

            val authAction = app.injector.instanceOf[AuthenticatedIdentifierAction]
            val controller = new Harness(authAction)
            val result     = controller.onPageLoad()(fakeRequest)

            status(result) mustBe SEE_OTHER
            redirectLocation(result).value mustBe controllers.routes.ErrorController.serviceUnavailable().url
          }
        }
      }
    }
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuthConnector)
    reset(mockEnrolmentStoreConnector)
  }
}

object AuthActionSpec {

  implicit class RetrievalsUtil[A](val retrieval: A) extends AnyVal {
    def `~`[B](anotherRetrieval: B): A ~ B = authClient.retrieve.~(retrieval, anotherRetrieval)
  }
}

class FakeFailingAuthConnector @Inject() (exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
