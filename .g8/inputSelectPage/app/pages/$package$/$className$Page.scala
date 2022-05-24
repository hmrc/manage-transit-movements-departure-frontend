package pages.$package$

import play.api.libs.json.JsPath
import pages.QuestionPage
import models.reference.$referenceClass$

case object $className$Page extends QuestionPage[$referenceClass$] {
  
  override def path: JsPath = JsPath \ toString
  
  override def toString: String = "$package$.$className;format="decap"$"
}
