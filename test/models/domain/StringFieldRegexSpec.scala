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

package models.domain

import base.SpecBase
import generators.Generators
import models.domain.StringFieldRegex.{telephoneNumberRegex, tirIdNumberRegex}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class StringFieldRegexSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  ".tirIdNumberRegex" - {
    val prefixLength = 8
    val suffixLength = 9
    val prefixGen: Arbitrary[String] =
      Arbitrary {
        for {
          startString <- stringsWithLength(3, Gen.alphaChar)
          middleNos   <- stringsWithLength(3, Gen.numChar)
        } yield s"$startString/$middleNos/"
      }

    "must match strings with correct prefix and suffix but under max length" in {
      val gen = for {
        prefix <- prefixGen.arbitrary
        suffix <- stringsWithMaxLength(suffixLength, Gen.numChar)
      } yield prefix + suffix

      forAll(gen) {
        validStrings =>
          tirIdNumberRegex.pattern.matcher(validStrings).matches mustEqual true
      }
    }

    "must not match strings with correct prefix and suffix but over max length" in {
      val gen = for {
        prefix <- prefixGen.arbitrary
        suffix <- stringsLongerThan(suffixLength, Gen.numChar)
      } yield prefix + suffix

      forAll(gen) {
        invalidString =>
          tirIdNumberRegex.pattern.matcher(invalidString).matches mustEqual false
      }
    }

    "must not match strings with correct prefix but invalid suffix" in {
      val gen = for {
        prefix <- prefixGen.arbitrary
        suffix <- stringsLongerThan(suffixLength, Gen.alphaChar)
      } yield prefix + suffix

      forAll(gen) {
        invalidString =>
          tirIdNumberRegex.pattern.matcher(invalidString).matches mustEqual false
      }
    }

    "must not match strings with wrong prefix" in {
      val gen = for {
        prefix <- stringsWithLength(prefixLength, Gen.alphaChar)
        suffix <- stringsWithLength(suffixLength, Gen.numChar)
      } yield prefix + suffix

      forAll(gen) {
        invalidString =>
          tirIdNumberRegex.pattern.matcher(invalidString).matches mustEqual false
      }
    }
  }

  ".telephoneNumberRegex" - {
    val validExamples = Seq(
      "+44 (0) 123 222 333 x 234",
      "+44(0)123-222-333",
      "+0785 999 1234",
      "+0785.999.1234",
      "+1-541-754-3010",
      "+(090) 636-48018"
    )

    "must match valid examples" in {
      forAll(Gen.oneOf(validExamples)) {
        validString =>
          telephoneNumberRegex.pattern.matcher(validString).matches mustEqual true
      }
    }

    "must not match if the telephone number contains a character other that 0-9,+,-,.,(,), ." in {
      val validCharacters: Seq[String] = "0123456789()+-. x".toSeq.map(_.toString)
      val generator: Gen[String]       = stringsExceptSpecificValues(validCharacters)

      forAll(generator) {
        invalidString =>
          telephoneNumberRegex.pattern.matcher(invalidString).matches mustEqual false
      }
    }
  }
}
