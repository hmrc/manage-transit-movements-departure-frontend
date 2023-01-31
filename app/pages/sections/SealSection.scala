package pages.sections

import models.Index
import play.api.libs.json.{JsObject, JsPath}

case class SealSection(equipmentIndex: Index, sealIndex: Index) extends Section[JsObject] {

  override def path: JsPath = SealsSection(equipmentIndex).path \ sealIndex.position

}
