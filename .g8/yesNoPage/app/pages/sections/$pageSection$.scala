package pages.$package$

import play.api.libs.json.JsPath
import pages.QuestionPage
import pages.sections.$pageSection$

case object $pageSection$ extends QuestionPage[Nothing] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "$navRoute$"
}
