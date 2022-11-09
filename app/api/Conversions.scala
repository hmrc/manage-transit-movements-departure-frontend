/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package api

import generated._
import models.journeyDomain.PreTaskListDomain
import play.api.libs.json.Json

import javax.xml.datatype.XMLGregorianCalendar
import scala.xml.NamespaceBinding

object Conversions {

  def cc004CType(preTaskListDomain: PreTaskListDomain): CC004CType = {
    // map these elements from preTaskListDomain
    val m1: MESSAGESequence                       = ???
    val to: TransitOperationType01                = ???
    val cod: CustomsOfficeOfDepartureType03       = ???
    val holder: HolderOfTheTransitProcedureType20 = ???

    CC004CType(m1, to, cod, holder)
  }

  // We start to run into problems writing json for the generated types
  implicit val XMLGregorianCalendarTypeJsonFormat = Json.format[XMLGregorianCalendar]
  implicit val transitOperationTypeJsonFormat     = Json.format[TransitOperationType06]

  def transitOperationType(preTaskListDomain: PreTaskListDomain): TransitOperationType06 =
    // TransitOperationType06 is not the correct node. It should be `TransitOperation`. Does this mean we need a custom writes?
    TransitOperationType06(
      preTaskListDomain.localReferenceNumber.value,
      preTaskListDomain.declarationType.toString,
      "A",
      preTaskListDomain.tirCarnetReference,
      None, // Dates need to be XML Gregorian
      preTaskListDomain.securityDetailsType.toString,
      Number0, // This is false? Need to translate booleans to this Flag type
      None,
      None,
      Number1, // This is true? Need to translate booleans to this Flag type
      None // Dates need to be XML Gregorian
    )

}
