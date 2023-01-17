package pages.sections

import play.api.libs.json.JsPath
import pages.QuestionPage

case object CarrierDetailsSection extends QuestionPage[Nothing] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "addContactYesNo"
}
