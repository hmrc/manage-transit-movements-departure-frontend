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

sealed trait ProcedureType extends Radioable[ProcedureType] {
  override val messageKeyPrefix: String = ProcedureType.messageKeyPrefix
}

object ProcedureType extends EnumerableType[ProcedureType] {

  case object Normal extends WithName("normal") with ProcedureType {
    override val code: String = "0"
  }

  case object Simplified extends WithName("simplified") with ProcedureType {
    override val code: String = "1"
  }

  val messageKeyPrefix: String = "procedureType"

  override val values: Seq[ProcedureType] = Seq(
    Normal,
    Simplified
  )
}
