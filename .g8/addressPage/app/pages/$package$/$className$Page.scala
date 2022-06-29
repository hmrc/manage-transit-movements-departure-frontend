package pages.$package$

import models.{Address, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.$pageSection$
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object $className$Page extends QuestionPage[Address] {
  
  override def path: JsPath = $pageSection$.path \ toString
  
  override def toString: String = "$className;format="decap"$"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(controllers.$package$.routes.$className;format="cap"$Controller.onPageLoad(userAnswers.lrn, mode))
}
