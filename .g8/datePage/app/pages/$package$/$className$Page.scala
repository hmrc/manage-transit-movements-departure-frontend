package pages.$package$

import play.api.libs.json.JsPath
import pages.QuestionPage
import pages.sections.$pageSection$
import java.time.LocalDate

case object $className$Page extends QuestionPage[LocalDate] {

  override def path: JsPath = $pageSection$.path \ toString

  override def toString: String = "$className;format="decap"$"
}
