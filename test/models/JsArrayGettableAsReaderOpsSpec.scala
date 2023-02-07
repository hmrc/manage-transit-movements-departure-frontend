/*
 * Copyright 2023 HM Revenue & Customs
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
import models.domain.JsArrayGettableAsReaderOps
import play.api.libs.json._
import queries.Gettable

class JsArrayGettableAsReaderOpsSpec extends SpecBase {

  "fieldReader" - {

    case object FakeSection extends Gettable[JsArray] {
      override def path: JsPath = __ \ "foo"
    }

    case class FakePage(index: Index) extends Gettable[String] {
      override def path: JsPath = __ \ "foo" \ index.position \ "bar"
    }

    "must read field from array and validate it" - {
      "when field is not inside every element in the array" in {
        val json = Json
          .parse("""
            |{
            |    "foo" : [
            |        {
            |            "bar" : "1"
            |        },
            |        {
            |            "baz" : "2"
            |        },
            |        {
            |            "bar" : "3"
            |        }
            |    ]
            |}
            |""".stripMargin)
          .as[JsObject]

        val userAnswers = emptyUserAnswers.copy(data = json)
        val result      = FakeSection.fieldReader(FakePage).run(userAnswers)
        result.value mustBe Seq("1", "3")
      }
    }
  }

}
