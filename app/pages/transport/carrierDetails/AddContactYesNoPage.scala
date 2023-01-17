package pages.transport.carrierDetails

import controllers.transport.carrierDetails.routes
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.CarrierDetailsSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object AddContactYesNoPage extends QuestionPage[Boolean] {

  override def path: JsPath = CarrierDetailsSection.path \ toString

  override def toString: String = "addContactYesNo"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.AddContactYesNoController.onPageLoad(userAnswers.lrn, mode))
}
