package pages.$package$

import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.$pageSection$
import play.api.libs.json.JsPath
import play.api.mvc.Call

import java.time.LocalDate

case object $className$Page extends QuestionPage[LocalDate] {

  override def path: JsPath = $pageSection$.path \ toString

  override def toString: String = "$className;format="decap"$"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(controllers.$package$.routes.$className;format="cap"$Controller.onPageLoad(userAnswers.lrn, mode))
}
