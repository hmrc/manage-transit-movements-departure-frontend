package pages.$package$

import models.$package$.$className$
import play.api.libs.json.JsPath
import pages.QuestionPage

case object $className$Page extends QuestionPage[$className$] {
  
  override def path: JsPath = JsPath \ toString
  
  override def toString: String = "$package$.$className;format="decap"$"
}
