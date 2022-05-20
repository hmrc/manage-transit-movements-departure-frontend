package pages.$package$

import models.Address
import play.api.libs.json.JsPath
import pages.QuestionPage

case object $className$Page extends QuestionPage[Address] {
  
  override def path: JsPath = JsPath \ toString
  
  override def toString: String = "$className;format="decap"$"
}
