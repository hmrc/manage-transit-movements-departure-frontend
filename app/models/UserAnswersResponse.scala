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
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

trait UserAnswersResponse

object UserAnswersResponse {

  case object NoAnswers extends UserAnswersResponse

  case class BadRequest(code: String) extends UserAnswersResponse

  object BadRequest {

    implicit val reads: Reads[BadRequest] = Json.reads[BadRequest]
  }

  case object NotAcceptable extends UserAnswersResponse

  case class Other(code: Int, message: String) extends UserAnswersResponse

  implicit val httpReads: HttpReads[UserAnswersResponse] =
    (_: String, _: String, response: HttpResponse) => {
      def validate[T <: UserAnswersResponse](implicit rds: Reads[T]): UserAnswersResponse =
        response.json
          .validate[T]
          .fold(
            errors => Other(response.status, s"Failed to validate json: $errors"),
            identity
          )

      response.status match {
        case OK                     => validate[UserAnswers]
        case NO_CONTENT | NOT_FOUND => NoAnswers
        case NOT_ACCEPTABLE         => NotAcceptable
        case BAD_REQUEST            => validate[BadRequest]
        case _                      => Other(response.status, response.body)
      }
    }
}
