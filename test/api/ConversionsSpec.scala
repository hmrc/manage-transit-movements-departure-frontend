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

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generated.TransitOperationType06
import generators.Generators
import models.DeclarationType._
import models.ProcedureType.Normal
import models.SecurityDetailsType
import models.journeyDomain.PreTaskListDomain
import models.reference.CustomsOffice
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import scalaxb._

import scala.xml.{NodeSeq, XML}

class ConversionsSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "transitOperationType" - {

    val xiCustomsOffice  = CustomsOffice("XI1", "Belfast", None)
    val carnetRef        = Gen.alphaNumStr.sample.value
    val securityDetails  = arbitrary[SecurityDetailsType].sample.value
    val detailsConfirmed = true

    "can be parsed from PreTaskListDomain" in {

      val preTaskSection = PreTaskListDomain(
        localReferenceNumber = emptyUserAnswers.lrn,
        officeOfDeparture = xiCustomsOffice,
        procedureType = Normal,
        declarationType = Option4,
        tirCarnetReference = Some(carnetRef),
        securityDetailsType = securityDetails,
        detailsConfirmed = detailsConfirmed
      )

      // API Example (transformed to xml)
      val expected: NodeSeq = XML.loadString(
        """<TransitOperation""" +
          """ xmlns:tns="http://ncts.dgtaxud.ec" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">""" +
          """<LRN>ABCD1234567890123</LRN>""" +
          """<declarationType>TIR</declarationType>""" +
          """<additionalDeclarationType>A</additionalDeclarationType>""" +
          s"""<TIRCarnetNumber>$carnetRef</TIRCarnetNumber>""" +
          """<presentationOfTheGoodsDateAndTime>2022-02-01T20:41:00.000Z</presentationOfTheGoodsDateAndTime>""" +
          s"""<security>${securityDetails.securityContentType}</security>""" +
          """<reducedDatasetIndicator>0</reducedDatasetIndicator>""" +
          """<bindingItinerary>1</bindingItinerary>""" +
          """<limitDate>2022-02-01T20:41:00.000Z</limitDate>""" +
          """</TransitOperation>""".stripMargin
      )

      val converted = toXML[TransitOperationType06](
        Conversions.transitOperation(preTaskSection),
        "TransitOperation",
        generated.defaultScope
      )

      converted shouldBe expected

    }
  }

}
