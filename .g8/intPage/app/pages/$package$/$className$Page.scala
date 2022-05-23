package pages.$package$

import play.api.libs.json.JsPath
import pages.QuestionPage

case object $className$Page extends QuestionPage[Int] {
  
  override def path: JsPath = JsPath \ toString
  
  override def toString: String = "$package$.$className;format="decap"$"
}
