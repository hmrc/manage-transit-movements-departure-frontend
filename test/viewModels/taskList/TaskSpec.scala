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

package viewModels.taskList

import base.SpecBase
import play.api.libs.json.{JsError, Json}

class TaskSpec extends SpecBase {

  "must deserialise from json" - {
    "when trader details" in {
      val json = Json.parse("""
          |{
          |    "section" : ".traderDetails",
          |    "status" : "completed"
          |}
          |""".stripMargin)

      val task = json.as[Task]

      task mustBe TraderDetailsTask(TaskStatus.Completed)
    }

    "when route details" in {
      val json = Json.parse("""
          |{
          |    "section" : ".routeDetails",
          |    "status" : "completed"
          |}
          |""".stripMargin)

      val task = json.as[Task]

      task mustBe RouteDetailsTask(TaskStatus.Completed)
    }

    "when transport details" in {
      val json = Json.parse("""
          |{
          |    "section" : ".transportDetails",
          |    "status" : "completed"
          |}
          |""".stripMargin)

      val task = json.as[Task]

      task mustBe TransportTask(TaskStatus.Completed)
    }

    "when guarantee details" in {
      val json = Json.parse("""
          |{
          |    "section" : ".guaranteeDetails",
          |    "status" : "completed"
          |}
          |""".stripMargin)

      val task = json.as[Task]

      task mustBe GuaranteeDetailsTask(TaskStatus.Completed)
    }

    "when something else" in {
      val json   = Json.parse("""
          |{
          |    "section" : "foo",
          |    "status": "completed"
          |}
          |""".stripMargin)
      val result = json.validate[Task]
      result mustBe a[JsError]
    }
  }

  "must serialise to json" in {
    val task = TraderDetailsTask(TaskStatus.Completed)
    val json = Json.toJson[Task](task)
    json mustBe Json.parse("""
        |{
        |    "section" : ".traderDetails",
        |    "status" : "completed"
        |}
        |""".stripMargin)
  }

}
