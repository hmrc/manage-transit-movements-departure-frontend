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

package forms.addItems.traderDetails

import forms.behaviours.BooleanFieldBehaviours
import models.Index
import play.api.data.FormError

class TraderDetailsConsigneeEoriKnownFormProviderSpec extends BooleanFieldBehaviours {

  private val requiredKey = "traderDetailsConsigneeEoriKnown.error.required"
  private val invalidKey  = "error.boolean"
  private val index       = Index(0)
  private val form        = new TraderDetailsConsigneeEoriKnownFormProvider()(index)

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey, Seq(index.display))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(index.display))
    )
  }
}
