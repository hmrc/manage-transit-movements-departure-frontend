package api

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.{DeclarationType, ProcedureType, SecurityDetailsType}
import models.journeyDomain.PreTaskListDomain
import models.reference.CustomsOffice
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary
import models.DeclarationType._
import models.ProcedureType.Normal
import play.api.libs.json.Json
import api.Conversions.transitOperationTypeJsonFormat // Need to pull in teh formatter to prevent compiler errors

class ConversionsSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "transitOperationType" - {

    val gbCustomsOffice           = CustomsOffice("GB1", "Dover", None)
    val xiCustomsOffice           = CustomsOffice("XI1", "Belfast", None)
    val carnetRef                 = Gen.alphaNumStr.sample.value
    val procedureType             = arbitrary[ProcedureType].sample.value
    val securityDetails           = arbitrary[SecurityDetailsType].sample.value
    val nonOption4DeclarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
    val detailsConfirmed          = true

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

      // Cache example (user answers)
      //
      //      "preTaskList" : {
      //        "officeOfDeparture" : {
      //        "id" : "GB000218",
      //        "name" : "Border Force, Port of Tyne"
      //        },
      //        "procedureType" : "normal",
      //        "declarationType" : "T",
      //        "securityDetailsType" : "entryAndExitSummaryDeclaration",
      //        "detailsConfirmed" : true
      //      }

      // API Example (transformed)
      val expected = Json.parse(s"""
                                   |{
                                   |  "TransitOperation": {
                                   |    "declarationType": "$Option4",
                                   |    "security": "$securityDetails",
                                   |    "LRN": "${emptyUserAnswers.lrn}",
                                   |    "TIRCarnet": "$carnetRef"
                                   |  }
                                   |}"""".stripMargin)

      Json.toJson(Conversions.transitOperationType(preTaskSection)) mustBe expected

    }
  }

}
