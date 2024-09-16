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

package forms.mappings

import generators.Generators
import models.{Enumerable, LocalReferenceNumber, Radioable, Selectable, SelectableList}
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.data.{Form, FormError}

class MappingsSpec extends AnyFreeSpec with Matchers with OptionValues with Generators with Mappings {

  "text" - {

    val testForm: Form[String] =
      Form(
        "value" -> text()(identity)
      )

    "must bind a valid string" in {
      val result = testForm.bind(Map("value" -> "foobar"))
      result.get mustEqual "foobar"
    }

    "must bind a valid string with trailing whitespace" in {
      val result = testForm.bind(Map("value" -> "foobar   "))
      result.get mustEqual "foobar"
    }

    "must not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must return a custom error message" in {
      val form   = Form("value" -> text("custom.error")(identity))
      val result = form.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "custom.error"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill("foobar")
      result.apply("value").value.value mustEqual "foobar"
    }
  }

  "formattedPostcode" - {

    val testForm: Form[String] =
      Form(
        "value" -> formattedPostcode()
      )

    "must bind a valid string" in {
      val result = testForm.bind(Map("value" -> "AB1 1AB"))
      result.get mustEqual "AB1 1AB"
    }

    "must bind a valid string with spaces" in {
      val result = testForm.bind(Map("value" -> "A B 1 1 A B"))
      result.get mustEqual "AB1 1AB"
    }

    "must not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must return a custom error message" in {
      val form   = Form("value" -> formattedPostcode("custom.error"))
      val result = form.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "custom.error"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill("AB1 1AB   ")
      result.apply("value").value.value mustEqual "AB1 1AB   "
    }
  }

  "boolean" - {

    val testForm: Form[Boolean] =
      Form(
        "value" -> boolean()
      )

    "must bind true" in {
      val result = testForm.bind(Map("value" -> "true"))
      result.get mustEqual true
    }

    "must bind false" in {
      val result = testForm.bind(Map("value" -> "false"))
      result.get mustEqual false
    }

    "must not bind a non-boolean" in {
      val result = testForm.bind(Map("value" -> "not a boolean"))
      result.errors must contain(FormError("value", "error.boolean"))
    }

    "must not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must unbind" in {
      val result = testForm.fill(true)
      result.apply("value").value.value mustEqual "true"
    }
  }

  "int" - {

    val testForm: Form[Int] =
      Form(
        "value" -> int()
      )

    "must bind a valid integer" in {
      val result = testForm.bind(Map("value" -> "1"))
      result.get mustEqual 1
    }

    "must not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill(123)
      result.apply("value").value.value mustEqual "123"
    }
  }

  "enumerable" - {

    sealed trait Foo extends Radioable[Foo]
    case object Bar extends Foo {
      override val code: String             = "bar"
      override val messageKeyPrefix: String = "mk.bar"
    }
    case object Baz extends Foo {
      override val code: String             = "baz"
      override val messageKeyPrefix: String = "mk.baz"
    }

    implicit val fooEnumerable: Enumerable[Foo] =
      Enumerable(
        Seq(Bar, Baz)
          .map(
            v => v.toString -> v
          ) *
      )

    val testForm = Form(
      "value" -> enumerable[Foo]()
    )

    "must bind a valid option" in {
      val result = testForm.bind(Map("value" -> "Bar"))
      result.get mustEqual Bar
    }

    "must not bind an invalid option" in {
      val result = testForm.bind(Map("value" -> "Not Bar"))
      result.errors must contain(FormError("value", "error.invalid"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill(Bar)
      result.apply("value").value.value mustEqual "bar"
    }
  }

  "selectable" - {

    case class Foo(value: String) extends Selectable

    val foo            = Foo("foo")
    val selectableList = SelectableList(Seq(foo))

    val testForm: Form[Foo] =
      Form(
        "value" -> selectable[Foo](selectableList)
      )

    "must bind a valid string" in {
      val result = testForm.bind(Map("value" -> "foo"))
      result.get mustEqual foo
    }

    "must not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind a country not in the list" in {
      val result = testForm.bind(Map("value" -> "FR"))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill(foo)
      result.apply("value").value.value mustEqual "foo"
    }
  }

  "lrn" - {

    val validLrn = LocalReferenceNumber("ABCD1234567890123").value

    def testForm(alreadyExists: Boolean): Form[LocalReferenceNumber] =
      Form(
        "value" -> lrn(
          "requiredKey",
          "lengthKey",
          "invalidCharactersKey",
          "invalidFormatKey"
        )
      )

    "must bind a valid lrn" in {
      val result = testForm(alreadyExists = false).bind(Map("value" -> "ABCD1234567890123"))
      result.get mustEqual validLrn
    }

    "must not bind an empty lrn" in {
      val result = testForm(alreadyExists = false).bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "requiredKey"))
    }

    "must not bind an empty map" in {
      val result = testForm(alreadyExists = false).bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "requiredKey"))
    }

    "must not bind an lrn which is too long" in {

      val invalidLengthString = "LOCALREFERENCENUMBER1234567890123456789"

      val result = testForm(alreadyExists = false).bind(Map("value" -> invalidLengthString))
      result.errors must contain(FormError("value", "lengthKey"))
    }

    "must not bind an lrn with invalid characters" in {
      val result = testForm(alreadyExists = false).bind(Map("value" -> "'#ABCD12345/.,;[)23"))
      result.errors must contain(FormError("value", "invalidCharactersKey"))
    }

    "must not bind an lrn with the incorrect format" in {
      val invalidFormats = Seq("-ABCD1234567890", "_ABCD1234567890")

      val invalidString = Gen.oneOf(invalidFormats).sample.value

      val result = testForm(alreadyExists = false).bind(Map("value" -> invalidString))
      result.errors must contain(FormError("value", "invalidFormatKey"))
    }

    "must unbind a valid value" in {
      val result = testForm(alreadyExists = false).fill(validLrn)
      result.apply("value").value.value mustEqual validLrn.toString
    }
  }

}
