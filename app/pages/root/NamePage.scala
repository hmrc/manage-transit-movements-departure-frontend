package pages.root

import controllers.root.routes
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.RootSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object NamePage extends QuestionPage[String] {

  override def path: JsPath = RootSection.path \ toString

  override def toString: String = "name"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.MyNewAddressController.onPageLoad(userAnswers.lrn, mode))
}
