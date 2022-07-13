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

package utils.cyaHelpers

import base.SpecBase
import uk.gov.hmrc.govukfrontend.views.Aliases._

class SummaryListRowHelperSpec extends SpecBase {

  class FakeHelper extends SummaryListRowHelper {

    override def formatAsCurrency(answer: BigDecimal): Content =
      super.formatAsCurrency(answer)
  }

  val helper = new FakeHelper()

  "formatAsCurrency" - {

    "when input is 0" - {
      "must return £0.00" in {
        val input = BigDecimal("0")
        helper.formatAsCurrency(input) mustBe Text("£0.00")
      }
    }

    "when input is 1" - {
      "must return £1.00" in {
        val input = BigDecimal("1")
        helper.formatAsCurrency(input) mustBe Text("£1.00")
      }
    }

    "when input is 1.2" - {
      "must return £1.20" in {
        val input = BigDecimal("1.2")
        helper.formatAsCurrency(input) mustBe Text("£1.20")
      }
    }

    "when input is 1.23" - {
      "must return £1.23" in {
        val input = BigDecimal("1.23")
        helper.formatAsCurrency(input) mustBe Text("£1.23")
      }
    }

    "when input is 12.34" - {
      "must return £12.34" in {
        val input = BigDecimal("12.34")
        helper.formatAsCurrency(input) mustBe Text("£12.34")
      }
    }

    "when input is 123.45" - {
      "must return £123.45" in {
        val input = BigDecimal("123.45")
        helper.formatAsCurrency(input) mustBe Text("£123.45")
      }
    }

    "when input is 1234.56" - {
      "must return £1,234.56" in {
        val input = BigDecimal("1234.56")
        helper.formatAsCurrency(input) mustBe Text("£1,234.56")
      }
    }

    "when input is 12345.67" - {
      "must return £12,345.67" in {
        val input = BigDecimal("12345.67")
        helper.formatAsCurrency(input) mustBe Text("£12,345.67")
      }
    }

    "when input is 123456.78" - {
      "must return £123,456.78" in {
        val input = BigDecimal("123456.78")
        helper.formatAsCurrency(input) mustBe Text("£123,456.78")
      }
    }

    "when input is 1234567.89" - {
      "must return £1,234,567.89" in {
        val input = BigDecimal("1234567.89")
        helper.formatAsCurrency(input) mustBe Text("£1,234,567.89")
      }
    }

    "when input is 12345678.9" - {
      "must return £12,345,678.90" in {
        val input = BigDecimal("12345678.9")
        helper.formatAsCurrency(input) mustBe Text("£12,345,678.90")
      }
    }

    "when input is 123456789" - {
      "must return £123,456,789.00" in {
        val input = BigDecimal("123456789")
        helper.formatAsCurrency(input) mustBe Text("£123,456,789.00")
      }
    }

    "when input is 1234567890" - {
      "must return £1,234,567,890.00" in {
        val input = BigDecimal("1234567890")
        helper.formatAsCurrency(input) mustBe Text("£1,234,567,890.00")
      }
    }

    "when input is 9999999999999999.99" - {
      "must return £9,999,999,999,999,999.99" in {
        val input = BigDecimal("9999999999999999.99")
        helper.formatAsCurrency(input) mustBe Text("£9,999,999,999,999,999.99")
      }
    }

    "when input is -1234.56" - {
      "must return -£1,234.56" in {
        val input = BigDecimal("-1234.56")
        helper.formatAsCurrency(input) mustBe Text("-£1,234.56")
      }
    }

    "when input is 1234.567" - {
      "must return £1,234.57" in {
        val input = BigDecimal("1234.567")
        helper.formatAsCurrency(input) mustBe Text("£1,234.57")
      }
    }

  }
}
