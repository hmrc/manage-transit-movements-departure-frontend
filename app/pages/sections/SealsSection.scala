package pages.sections

import models.Index
import pages.sections.transport.EquipmentSection
import play.api.libs.json.{JsArray, JsPath}

case class SealsSection(equipmentIndex: Index) extends Section[JsArray] {

  override def path: JsPath = EquipmentSection(equipmentIndex).path \ toString

  override def toString: String = "seals"

}
