package pages.root

import controllers.root.routes
import models.reference.Country
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.RootSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object CountryPage extends QuestionPage[Country] {

  override def path: JsPath = RootSection.path \ toString

  override def toString: String = "country"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.MyNewAddressController.onPageLoad(userAnswers.lrn, mode))
}
