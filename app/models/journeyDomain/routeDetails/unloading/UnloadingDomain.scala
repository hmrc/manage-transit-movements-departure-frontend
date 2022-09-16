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

package models.journeyDomain.routeDetails.unloading

import cats.implicits._
import models.SecurityDetailsType.ExitSummaryDeclarationSecurityDetails
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.JourneyDomainModel
import models.reference.UnLocode
import pages.preTaskList.SecurityDetailsTypePage
import pages.routeDetails.unloading.AddPlaceOfUnloadingPage

case class UnloadingDomain(
  unLocode: Option[UnLocode]
) extends JourneyDomainModel

object UnloadingDomain {
  // TODO: replace with next page

  implicit val userAnswersReader: UserAnswersReader[UnloadingDomain] =
    SecurityDetailsTypePage.reader.flatMap {
      case ExitSummaryDeclarationSecurityDetails =>
        AddPlaceOfUnloadingPage
          .filterOptionalDependent(identity)(UserAnswersReader(UnLocode("GB", "name1")))
          .map(UnloadingDomain.apply)
    }

}
