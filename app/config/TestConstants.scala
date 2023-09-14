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

package config

import config.Constants.{T2F, TIR}
import models.DeclarationType

object TestConstants {
  val declarationType1: DeclarationType = DeclarationType("T1", "t1 description")
  val declarationType2: DeclarationType = DeclarationType("T2", "t2 description")
  val declarationType3: DeclarationType = DeclarationType(T2F, "t2f description")
  val declarationType4: DeclarationType = DeclarationType(TIR, "tir description")
  val declarationType5: DeclarationType = DeclarationType("T", "t description")

  val declarationTypeValues: Seq[DeclarationType] = Seq(
    declarationType1,
    declarationType2,
    declarationType3,
    declarationType4,
    declarationType5
  )
}
