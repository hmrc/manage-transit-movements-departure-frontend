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

package forms

import forms.behaviours.StringFieldBehaviours
import models.TransportModeList
import models.reference.TransportMode
import play.api.data.FormError

class ModeCrossingBorderFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey = "modeCrossingBorder.error.required"
  private val maxLength   = 100

  private val transportModeList = TransportModeList(
    Seq(
      TransportMode("1", "Sea transport"),
      TransportMode("10", "Sea transport")
    )
  )
  private val form = new ModeCrossingBorderFormProvider()(transportModeList)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "Not bind if code does not exist in the TransportModeList" in {

      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")

      field.errors mustNot be(empty)
    }

    "Bind code which is in the TransportModeList" in {

      val boundForm = form.bind(Map("value" -> "1"))
      val field     = boundForm("value")

      field.errors mustBe empty
    }
  }
}
