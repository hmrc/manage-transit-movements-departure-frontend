package controllers.transport.equipment

import base.{AppWithDefaultMockFixtures, SpecBase}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.equipment.AddAnotherTransportEquipmentView

class AddAnotherTransportEquipmentControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private lazy val addAnotherTransportEquipmentRoute = routes.AddAnotherTransportEquipmentController.onPageLoad(lrn).url

  "AddAnotherTransportEquipment Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, addAnotherTransportEquipmentRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[AddAnotherTransportEquipmentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn)(request, messages).toString
    }
  }
}
