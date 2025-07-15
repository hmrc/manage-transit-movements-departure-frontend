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

package models

import base.SpecBase
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, Json}

class DepartureMessagesSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "reads" - {
    "must deserialise" - {
      "when json contains list of messages" in {
        val json = Json.parse(s"""
             |{
             |  "messages" : [
             |    {
             |      "type" : "IE015"
             |    },
             |    {
             |      "type" : "IE928"
             |    },
             |    {
             |      "type" : "IE028"
             |    }
             |  ]
             |}
             |""".stripMargin)

        val result = json.validate[DepartureMessages]

        val expectedResult = DepartureMessages(
          Seq(
            DepartureMessage("IE015"),
            DepartureMessage("IE928"),
            DepartureMessage("IE028")
          )
        )

        result.get.mustEqual(expectedResult)
      }
    }

    "must fail to deserialise" - {
      "when json is in unexpected shape" in {
        val json = Json.parse("""
            |{
            |  "foo" : "bar"
            |}
            |""".stripMargin)

        val result = json.validate[DepartureMessages]

        result mustBe a[JsError]
      }
    }
  }

  "contains" - {
    "when given a list of messages" - {
      val messages = DepartureMessages(
        Seq(
          DepartureMessage("IE015"),
          DepartureMessage("IE928"),
          DepartureMessage("IE028")
        )
      )

      "must return true" - {
        "when checking if messages contains IE015" in {
          messages.contains("IE015").mustEqual(true)
        }

        "when checking if messages contains IE928" in {
          messages.contains("IE928").mustEqual(true)
        }

        "when checking if messages contains IE028" in {
          messages.contains("IE028").mustEqual(true)
        }
      }

      "must return false" - {
        "when given message type doesn't exist in list of messages" in {
          messages.contains("IE029").mustEqual(false)
        }
      }
    }
  }
}
