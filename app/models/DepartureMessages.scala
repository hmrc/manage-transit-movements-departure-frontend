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

import play.api.http.Status.*
import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.http.{HttpReads, HttpResponse, UpstreamErrorResponse}

case class DepartureMessages(messages: Seq[DepartureMessage]) {

  def contains(`type`: String): Boolean = messages.exists(_.`type` == `type`)
}

object DepartureMessages {

  def apply(): DepartureMessages =
    new DepartureMessages(Seq.empty[DepartureMessage])

  implicit lazy val reads: Reads[DepartureMessages] = Json.reads[DepartureMessages]

  implicit lazy val httpReads: HttpReads[DepartureMessages] = {
    (_: String, _: String, response: HttpResponse) =>
      response.status match {
        case OK                     => response.json.as[DepartureMessages]
        case NO_CONTENT | NOT_FOUND => DepartureMessages()
        case _                      => throw UpstreamErrorResponse(response.body, response.status)
      }
  }
}
