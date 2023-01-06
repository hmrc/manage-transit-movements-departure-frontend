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
import models.UserAnswers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import pages.preTaskList.{DeclarationTypePage, SecurityDetailsTypePage}
import pages.traderDetails.consignment.ApprovedOperatorPage
import scalaxb._

import scala.xml.{NodeSeq, XML}

class ConversionsSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "transitOperationType" - {

    "can be parsed from PreTaskListDomain" in {

      val preTask: UserAnswers       = arbitraryPreTaskListAnswers(emptyUserAnswers).sample.value
      val traderDetails: UserAnswers = arbitraryTraderDetailsAnswers(preTask).sample.value
      val uA: UserAnswers            = arbitraryRouteDetailsAnswers(traderDetails).sample.value

      val expected: NodeSeq = XML.loadString(
        """<TransitOperation""" +
          """ xmlns:tns="http://ncts.dgtaxud.ec" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">""" +
          s"""<LRN>${uA.lrn.value}</LRN>""" +
          s"""<declarationType>${uA.get(DeclarationTypePage).get}</declarationType>""" +
          """<additionalDeclarationType>A</additionalDeclarationType>""" +
          s"""<security>${uA.get(SecurityDetailsTypePage).get.securityContentType}</security>""" +
          s"""<reducedDatasetIndicator>${uA.get(ApprovedOperatorPage).get match {
            case true => 1
            case _    => 0
          }}</reducedDatasetIndicator>""" +
          """<bindingItinerary>1</bindingItinerary>""" +
          """</TransitOperation>""".stripMargin
      )

      val tryConv: Either[String, TransitOperationType06] = Conversions.transitOperation(uA)

      val xml: NodeSeq = tryConv match {
        case Left(value)  => throw new Error(value)
        case Right(value) => toXML[TransitOperationType06](value, "TransitOperation", generated.defaultScope)
      }

      xml shouldBe expected

    }
  }

}
