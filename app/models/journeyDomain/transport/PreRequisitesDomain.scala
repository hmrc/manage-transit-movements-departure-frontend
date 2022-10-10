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

package models.journeyDomain.transport

import cats.implicits._
import models.DeclarationType.Option4
import models.domain.{UserAnswersReader, _}
import models.journeyDomain.JourneyDomainModel
import models.reference.Country
import pages.preTaskList.DeclarationTypePage
import pages.transport.preRequisites._

case class PreRequisitesDomain(
  ucr: Option[String],
  countryOfDispatch: Option[Country]
) extends JourneyDomainModel

object PreRequisitesDomain {

  implicit val countryOfDispatchReader: UserAnswersReader[Option[Country]] =
    DeclarationTypePage.filterOptionalDependent(_ == Option4)(CountryOfDispatchPage.reader)

  implicit val userAnswersReader: UserAnswersReader[PreRequisitesDomain] = (
    SameUcrYesNoPage.filterOptionalDependent(identity)(UniqueConsignmentReferencePage.reader),
    countryOfDispatchReader
  ).tupled.map((PreRequisitesDomain.apply _).tupled)
}
