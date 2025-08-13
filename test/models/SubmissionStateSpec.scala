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
import models.SubmissionState.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import play.api.libs.json.{JsString, Json}

class SubmissionStateSpec extends SpecBase with Generators {

  "submissionState" - {

    "must deserialise" in {
      forAll(arbitrary[SubmissionState]) {
        state =>
          JsString(state.toString).as[SubmissionState] mustEqual state
      }
    }

    "must serialise" in {
      forAll(arbitrary[SubmissionState]) {
        state =>
          Json.toJson(state) mustEqual JsString(state.toString)
      }
    }

    "must return showErrorContent" - {
      "when NotSubmitted must be false" in {
        val value = SubmissionState.NotSubmitted
        value.showErrorContent mustEqual false
      }

      "when Submitted must be false" in {
        val value = SubmissionState.Submitted
        value.showErrorContent mustEqual false
      }

      "when RejectedPendingChanges must be true" in {
        val value = SubmissionState.RejectedPendingChanges
        value.showErrorContent mustEqual true
      }

      "when Amendment must be true" in {
        val value = SubmissionState.Amendment
        value.showErrorContent mustEqual true
      }

      "when GuaranteeAmendment must be true" in {
        val value = SubmissionState.GuaranteeAmendment
        value.showErrorContent mustEqual true
      }
    }
  }
}
