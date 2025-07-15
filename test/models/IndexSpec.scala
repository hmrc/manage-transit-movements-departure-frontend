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

import base.SpecBase
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class IndexSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "display" - {
    "must return correct Int" in {
      Index(0).display mustEqual 1
    }
  }

  "pathBindable" - {
    val binder = Index.pathBindable
    val key    = "index"

    "bind a valid index" in {
      binder.bind(key, "1").value mustEqual Index(0)
    }

    "fail to bind an index with negative value" in {
      binder.bind(key, "-1").left.value mustEqual "Index binding failed"
    }

    "unbind an index" in {
      binder.unbind(key, Index(0)) mustEqual "1"
    }
  }

  "isFirst" - {
    "must return true" - {
      "when position is 0" in {
        val position = 0
        val index    = Index(position)
        val result   = index.isFirst
        result.mustEqual(true)
      }
    }

    "must return false" - {
      "when position is not 0" in {
        forAll(positiveInts) {
          position =>
            val index  = Index(position)
            val result = index.isFirst
            result.mustEqual(false)
        }
      }
    }
  }
}
