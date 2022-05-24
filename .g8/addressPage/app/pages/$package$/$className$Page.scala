package pages.$package$

import models.Address
import play.api.libs.json.JsPath
import pages.QuestionPage
import pages.sections.$pageSection$

case object $className$Page extends QuestionPage[Address] {
  
  override def path: JsPath = $pageSection$.path \ toString
  
  override def toString: String = "$className;format="decap"$"
}
