package pages.traderDetails.holderOfTransit

import play.api.libs.json.JsPath
import pages.QuestionPage
import pages.sections.HolderOfTransitSection

case object TirIdentificationNoControllerPage extends QuestionPage[String] {

  override def path: JsPath = HolderOfTransitSection.path \ toString

  override def toString: String = "tirIdentificationNoController"
}
