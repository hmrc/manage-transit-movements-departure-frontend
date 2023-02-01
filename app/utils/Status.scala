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

package utils

import play.api.libs.json.{Format, Json}

sealed trait Status

object Status extends Enumeration {

  type Status = Value

  val Draft: Status     = Value("draft")
  val Submitted: Status = Value("submitted")
  val Rejected: Status  = Value("rejected")

  implicit val format: Format[Status] = Json.formatEnum(this)
}
