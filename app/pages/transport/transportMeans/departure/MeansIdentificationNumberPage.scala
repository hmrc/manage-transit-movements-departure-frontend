package pages.transport.transportMeans.departure

import controllers.transport.transportMeans.departure.routes
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.TransportMeans
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object MeansIdentificationNumberPage extends QuestionPage[String] {

  override def path: JsPath = TransportMeans.path \ toString

  override def toString: String = "meansIdentificationNumber"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.MeansIdentificationNumberController.onPageLoad(userAnswers.lrn, mode))
}
