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

package models

import play.api.libs.json.{Format, Json}

sealed trait SubmissionState

object SubmissionState extends Enumeration {

  type SubmissionState = Value

  // IE015 has not yet been submitted
  val NotSubmitted: SubmissionState.Value = Value("notSubmitted")

  // IE013/IE015 has been submitted
  val Submitted: SubmissionState.Value = Value("submitted")

  // IE015 has been rejected - Existing declaration can be resubmitted
  //  IE015 -> IE056
  //  IE015 -> IE028 -> IE056 (new LRN needed)
  val RejectedPendingChanges: SubmissionState.Value = Value("rejectedPendingChanges")

  // IE015 has been rejected with an IE056 (Rejection from office of departure) message
  //  IE013 is submitted with amendmentTypeFlag 0 (false)
  val Amendment: SubmissionState.Value = Value("amendment")

  // IE015 has been rejected with an IE055 (Guarantee not valid) message
  //  IE013 is submitted with amendmentTypeFlag 1 (true)
  val GuaranteeAmendment: SubmissionState.Value = Value("guaranteeAmendment")

  implicit val format: Format[SubmissionState.Value] = Json.formatEnum(SubmissionState)

  implicit class RichSubmissionState(value: Value) {

    def showErrorContent: Boolean =
      Seq(RejectedPendingChanges, Amendment, GuaranteeAmendment).contains(value)
  }
}
