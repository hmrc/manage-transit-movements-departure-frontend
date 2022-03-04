/*
 * Copyright 2022 HM Revenue & Customs
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

package derivable

import models.Index
import play.api.libs.json.{JsObject, JsPath}
import queries.Constants.{items, previousReferences}

case class DeriveNumberOfPreviousAdministrativeReferences(itemIndex: Index) extends Derivable[List[JsObject], Int] {

  override val derive: List[JsObject] => Int = _.size

  override def path: JsPath = JsPath \ items \ itemIndex.position \ previousReferences
}
