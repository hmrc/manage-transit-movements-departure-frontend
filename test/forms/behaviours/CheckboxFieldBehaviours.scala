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

package forms.behaviours

import forms.FormSpec
import play.api.data.{Form, FormError}

trait CheckboxFieldBehaviours extends FormSpec {

  def checkboxField[T](form: Form[?], fieldName: String, validValues: Seq[T], invalidError: FormError): Unit = {
    for {
      (value, i) <- validValues.zipWithIndex
    } yield s"binds `$value` successfully" in {
      val data = Map(
        s"$fieldName[$i]" -> value.toString
      )
      form.bind(data).get mustEqual Set(value)
    }

    "must fail to bind when the answer is invalid" in {
      val data = Map(
        s"$fieldName[0]" -> "invalid value"
      )
      form.bind(data).errors must contain(invalidError)
    }
  }

  def mandatoryCheckboxField(form: Form[?], fieldName: String, requiredKey: String): Unit = {

    "must fail to bind when no answers are selected" in {
      val data = Map.empty[String, String]
      form.bind(data).errors must contain(FormError(s"$fieldName", requiredKey))
    }

    "must fail to bind when blank answer provided" in {
      val data = Map(
        s"$fieldName[0]" -> ""
      )
      form.bind(data).errors must contain(FormError(s"$fieldName[0]", requiredKey))
    }
  }
}
