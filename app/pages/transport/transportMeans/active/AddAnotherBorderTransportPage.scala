package pages.transport.transportMeans.active

import controllers.transport.transportMeans.active.routes
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.TransportSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object AddAnotherBorderTransportPage extends QuestionPage[Boolean] {

  override def path: JsPath = TransportSection.path \ toString

  override def toString: String = "addAnotherBorderTransport"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.AddAnotherBorderTransportController.onPageLoad(userAnswers.lrn, mode))
}
