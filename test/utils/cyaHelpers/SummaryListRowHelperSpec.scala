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

package utils.cyaHelpers

import base.SpecBase
import models.reference.CurrencyCode
import uk.gov.hmrc.govukfrontend.views.Aliases._

class SummaryListRowHelperSpec extends SpecBase {

  private class FakeHelper extends SummaryListRowHelper {

    override def formatAsCurrency(answer: BigDecimal, currencyCode: CurrencyCode): Content =
      super.formatAsCurrency(answer, currencyCode)
  }

  private val helper = new FakeHelper()

  "formatAsCurrency" - {

    "when currency can be formatted" - {
      val currencyCode = CurrencyCode("GBP", Some("Sterling"))

      "when input is 0" - {
        "must return £0.00" in {
          val input = BigDecimal("0")
          helper.formatAsCurrency(input, currencyCode) mustBe Text("£0.00")
        }
      }

      "when input is 1" - {
        "must return £1.00" in {
          val input = BigDecimal("1")
          helper.formatAsCurrency(input, currencyCode) mustBe Text("£1.00")
        }
      }

      "when input is 1.2" - {
        "must return £1.20" in {
          val input = BigDecimal("1.2")
          helper.formatAsCurrency(input, currencyCode) mustBe Text("£1.20")
        }
      }

      "when input is 1.23" - {
        "must return £1.23" in {
          val input = BigDecimal("1.23")
          helper.formatAsCurrency(input, currencyCode) mustBe Text("£1.23")
        }
      }

      "when input is -1234.56" - {
        "must return -£1,234.56" in {
          val input = BigDecimal("-1234.56")
          helper.formatAsCurrency(input, currencyCode) mustBe Text("-£1,234.56")
        }
      }

      "when input is 1234.567" - {
        "must return £1,234.57" in {
          val input = BigDecimal("1234.567")
          helper.formatAsCurrency(input, currencyCode) mustBe Text("£1,234.57")
        }
      }
    }

    "when currency can't be formatted" - {
      val currencyCode = CurrencyCode("FOO", None)

      "when input is 1000" - {
        "must return 1000 FOO" in {
          val input = BigDecimal("1000")
          helper.formatAsCurrency(input, currencyCode) mustBe Text("1000 FOO")
        }
      }
    }
  }
}
