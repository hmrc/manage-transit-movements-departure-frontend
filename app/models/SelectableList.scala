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

import cats.data.NonEmptySet
import services.RichNonEmptySet

case class SelectableList[T <: Selectable](values: Seq[T])

object SelectableList {

  def apply[T <: Selectable](seq: Seq[T]): SelectableList[T] =
    new SelectableList[T](seq)

  def apply[T <: Selectable](nonEmptySet: NonEmptySet[T]): SelectableList[T] =
    apply(nonEmptySet.toSeq)
}
