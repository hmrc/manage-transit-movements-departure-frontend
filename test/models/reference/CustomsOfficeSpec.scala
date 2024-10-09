/*
 * Copyright 2024 HM Revenue & Customs
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

package models.reference

import base.SpecBase
import cats.data.NonEmptySet
import models.SelectableList
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, Json}
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

class CustomsOfficeSpec extends SpecBase with ScalaCheckPropertyChecks {

  "CustomsOffice" - {

    "must serialise" - {
      "when phone number defined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr, Gen.alphaNumStr, Gen.alphaNumStr) {
          (id, name, phoneNumber, countryId) =>
            val customsOffice = CustomsOffice(id, name, Some(phoneNumber), countryId)
            Json.toJson(customsOffice) mustBe Json.parse(s"""
                |{
                |  "id": "$id",
                |  "name": "$name",
                |  "phoneNumber": "$phoneNumber",
                |  "countryId": "$countryId"
                |}
                |""".stripMargin)
        }
      }

      "when phone number undefined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr, Gen.alphaNumStr) {
          (id, name, countryId) =>
            val customsOffice = CustomsOffice(id, name, None, countryId)
            Json.toJson(customsOffice) mustBe Json.parse(s"""
                |{
                |  "id": "$id",
                |  "name": "$name",
                |  "countryId": "$countryId"
                |}
                |""".stripMargin)
        }
      }
    }

    "must deserialise" - {
      "when phone number defined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr, Gen.alphaNumStr, Gen.alphaNumStr) {
          (id, name, phoneNumber, countryId) =>
            val customsOffice = CustomsOffice(id, name, Some(phoneNumber), countryId)
            Json
              .parse(s"""
                |{
                |  "id": "$id",
                |  "name": "$name",
                |  "phoneNumber": "$phoneNumber",
                |  "countryId": "$countryId"
                |}
                |""".stripMargin)
              .as[CustomsOffice] mustBe customsOffice
        }
      }

      "when phone number undefined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr, Gen.alphaNumStr) {
          (id, name, countryId) =>
            val customsOffice = CustomsOffice(id, name, None, countryId)
            Json
              .parse(s"""
                |{
                |  "id": "$id",
                |  "name": "$name",
                |  "countryId": "$countryId"
                |}
                |""".stripMargin)
              .as[CustomsOffice] mustBe customsOffice
        }
      }
    }

    "must convert to select item" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr, Gen.alphaNumStr, arbitrary[Boolean]) {
        (id, name, countryId, selected) =>
          val customsOffice = CustomsOffice(id, name, None, countryId)
          customsOffice.toSelectItem(selected) mustBe SelectItem(Some(id), s"$name ($id)", selected)
      }
    }

    "must format as string" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr, Gen.alphaNumStr) {
        (id, name, countryId) =>
          val customsOffice = CustomsOffice(id, name, None, countryId)
          customsOffice.toString mustBe s"$name ($id)"
      }
    }

    "must order" in {
      val customsOffice1 = CustomsOffice("FRCONF03", "TEST CONF 02", None, "FR")
      val customsOffice2 = CustomsOffice("FRCONF01", "TEST CONF 02", None, "FR")
      val customsOffice3 = CustomsOffice("FR620001", "Calais port tunnel bureau", None, "FR")
      val customsOffice4 = CustomsOffice("FR590002", "Calais port tunnel bureau", None, "FR")

      val customsOffices = NonEmptySet.of(customsOffice1, customsOffice2, customsOffice3, customsOffice4)

      val result = SelectableList(customsOffices).values

      result mustBe Seq(
        customsOffice4,
        customsOffice3,
        customsOffice2,
        customsOffice1
      )
    }

    "listReads" - {
      "must read list of customs offices" - {
        "when offices have distinct IDs" in {
          val json = Json.parse("""
                                  |[
                                  |  {
                                  |    "id" : "AD000001",
                                  |    "name" : "CUSTOMS OFFICE SANT JULIÀ DE LÒRIA",
                                  |    "countryId" : "AD",
                                  |    "languageCode" : "EN"
                                  |  },
                                  |  {
                                  |    "id" : "AD000002",
                                  |    "name" : "DCNJ PORTA",
                                  |    "countryId" : "AD",
                                  |    "languageCode" : "EN"
                                  |  },
                                  |  {
                                  |    "id": "IT261101",
                                  |    "name": "PASSO NUOVO",
                                  |    "countryId": "IT",
                                  |    "languageCode": "IT"
                                  |  }
                                  |]
                                  |""".stripMargin)

          val result = json.as[List[CustomsOffice]]

          result mustBe List(
            CustomsOffice("AD000001", "CUSTOMS OFFICE SANT JULIÀ DE LÒRIA", None, "AD"),
            CustomsOffice("AD000002", "DCNJ PORTA", None, "AD"),
            CustomsOffice("IT261101", "PASSO NUOVO", None, "IT")
          )
        }

        "when offices have duplicate IDs must prioritise the office with an EN language code" in {
          val json = Json.parse("""
                                  |[
                                  |  {
                                  |    "id" : "AD000001",
                                  |    "name" : "CUSTOMS OFFICE SANT JULIÀ DE LÒRIA",
                                  |    "countryId" : "AD",
                                  |    "languageCode" : "EN"
                                  |  },
                                  |  {
                                  |    "id" : "AD000001",
                                  |    "name" : "ADUANA DE ST. JULIÀ DE LÒRIA",
                                  |    "countryId" : "AD",
                                  |    "languageCode" : "ES"
                                  |  },
                                  |  {
                                  |    "id" : "AD000001",
                                  |    "name" : "BUREAU DE SANT JULIÀ DE LÒRIA",
                                  |    "countryId" : "AD",
                                  |    "languageCode" : "FR"
                                  |  },
                                  |  {
                                  |    "id" : "AD000002",
                                  |    "name" : "DCNJ PORTA",
                                  |    "countryId" : "AD",
                                  |    "languageCode" : "FR"
                                  |  },
                                  |  {
                                  |    "id" : "AD000002",
                                  |    "name" : "DCNJ PORTA",
                                  |    "countryId" : "AD",
                                  |    "languageCode" : "ES"
                                  |  },
                                  |  {
                                  |    "id" : "AD000002",
                                  |    "name" : "DCNJ PORTA",
                                  |    "countryId" : "AD",
                                  |    "languageCode" : "EN"
                                  |  },
                                  |  {
                                  |    "id": "IT261101",
                                  |    "name": "PASSO NUOVO",
                                  |    "countryId": "IT",
                                  |    "languageCode": "IT"
                                  |  }
                                  |]
                                  |""".stripMargin)

          val result = json.as[List[CustomsOffice]]

          result mustBe List(
            CustomsOffice("AD000001", "CUSTOMS OFFICE SANT JULIÀ DE LÒRIA", None, "AD"),
            CustomsOffice("AD000002", "DCNJ PORTA", None, "AD"),
            CustomsOffice("IT261101", "PASSO NUOVO", None, "IT")
          )
        }
      }

      "must fail to read list of customs offices" - {
        "when not an array" in {
          val json = Json.parse("""
                                  |{
                                  |  "foo" : "bar"
                                  |}
                                  |""".stripMargin)

          val result = json.validate[List[CustomsOffice]]

          result mustBe JsError("Expected customs offices to be in a JsArray")
        }
      }
    }
  }

}
