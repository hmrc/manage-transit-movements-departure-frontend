package controllers.transport.equipment

import base.{AppWithDefaultMockFixtures, SpecBase}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.equipment.AddAnotherGoodsItemNumberView

class AddAnotherGoodsItemNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private lazy val addAnotherGoodsItemNumberRoute = routes.AddAnotherGoodsItemNumberController.onPageLoad(lrn).url

  "AddAnotherGoodsItemNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, addAnotherGoodsItemNumberRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[AddAnotherGoodsItemNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn)(request, messages).toString
    }
  }
}
