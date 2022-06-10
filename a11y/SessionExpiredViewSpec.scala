import a11ySpecBase.A11ySpecBase
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.SessionExpiredView

class SessionExpiredViewSpec extends A11ySpecBase {

  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder()
      .configure()
      .build()
  }

  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/foo")

  implicit lazy val messages: Messages = {
    val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
    messagesApi.preferred(fakeRequest)
  }

  "the session expired view" must {
    val view = app.injector.instanceOf[SessionExpiredView]
    val content = view()

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks
    }
  }
}
