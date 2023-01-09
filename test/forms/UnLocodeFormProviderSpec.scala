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

package forms

import forms.behaviours.StringFieldBehaviours
import models.UnLocodeList
import models.reference.UnLocode
import org.scalacheck.Gen
import play.api.data.FormError

class UnLocodeFormProviderSpec extends StringFieldBehaviours {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val requiredKey = s"$prefix.error.required"
  private val maxLength   = 8

  private val unLocodes = UnLocodeList(
    Seq(
      UnLocode("ADALV", "Andorra la Vella"),
      UnLocode("ADCAN", "Canillo")
    )
  )

  private val form = new UnLocodeFormProvider()(prefix, unLocodes)

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

    "not bind if unLocode unLocodeExtendedCode does not exist in the unLocode list" in {

      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind an unLocode id which is in the list" in {

      val boundForm = form.bind(Map("value" -> "ADALV"))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }
}
