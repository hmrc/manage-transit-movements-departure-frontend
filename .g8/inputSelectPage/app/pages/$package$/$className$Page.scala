package pages.$package$

import play.api.libs.json.JsPath
import pages.QuestionPage
import pages.sections.$pageSection$
import models.reference.$referenceClass$

case object $className$Page extends QuestionPage[$referenceClass$] {

  override def path: JsPath = $pageSection$.path \ toString

  override def toString: String = "$className;format="decap"$"
}
