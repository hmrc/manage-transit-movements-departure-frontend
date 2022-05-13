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

package models

sealed trait SecurityDetailsType
sealed trait SecurityDetailsNeededType extends SecurityDetailsType

object SecurityDetailsType extends RadioModel[SecurityDetailsType] {

  case object NoSecurityDetails extends WithName("noSecurity") with SecurityDetailsType
  case object EntrySummaryDeclarationSecurityDetails extends WithName("entrySummaryDeclaration") with SecurityDetailsNeededType
  case object ExitSummaryDeclarationSecurityDetails extends WithName("exitSummaryDeclaration") with SecurityDetailsNeededType
  case object EntryAndExitSummaryDeclarationSecurityDetails extends WithName("entryAndExitSummaryDeclaration") with SecurityDetailsNeededType

  override val messageKeyPrefix: String = "securityDetailsType"

  override val values: Seq[SecurityDetailsType] = Seq(
    NoSecurityDetails,
    EntrySummaryDeclarationSecurityDetails,
    ExitSummaryDeclarationSecurityDetails,
    EntryAndExitSummaryDeclarationSecurityDetails
  )
}
