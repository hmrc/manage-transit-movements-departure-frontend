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

sealed trait AdditionalDeclarationType extends Radioable[AdditionalDeclarationType] {
  override val messageKeyPrefix: String = AdditionalDeclarationType.messageKeyPrefix
}

object AdditionalDeclarationType extends EnumerableType[AdditionalDeclarationType] {

  case object Standard extends WithName("standard") with AdditionalDeclarationType
  case object Prelodged extends WithName("pre-lodged") with AdditionalDeclarationType

  val messageKeyPrefix: String = "additionalDeclarationType"

  override val values: Seq[AdditionalDeclarationType] = Seq(
    Standard,
    Prelodged
  )
}
