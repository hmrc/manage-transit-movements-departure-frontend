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

package models.transport.authorisations

import models.{RadioModel, WithName}
import play.api.i18n.Messages

sealed trait AuthorisationType {

  def asString(implicit messages: Messages): String =
    messages(s"${AuthorisationType.messageKeyPrefix}.$this")

}

object AuthorisationType extends RadioModel[AuthorisationType] {

  case object ACR extends WithName("ACR") with AuthorisationType
  case object SSE extends WithName("SSE") with AuthorisationType
  case object TRD extends WithName("TRD") with AuthorisationType

  override val messageKeyPrefix: String = "transport.authorisations.authorisationType"

  val values: Seq[AuthorisationType] = Seq(
    ACR,
    SSE,
    TRD
  )
}
