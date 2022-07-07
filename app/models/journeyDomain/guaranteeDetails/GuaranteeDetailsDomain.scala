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

package models.journeyDomain.guaranteeDetails

import cats.implicits._
import models.domain.{UserAnswersReader, _}
import models.journeyDomain.JourneyDomainModel
import models.{Index, UserAnswers}
import pages.guaranteeDetails.GuaranteeTypePage
import pages.sections.GuaranteeDetailsSection
import play.api.mvc.Call

case class GuaranteeDetailsDomain(
  guarantees: Seq[GuaranteeDomain]
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers): Option[Call] =
    None // TODO - update to section summary when built
}

object GuaranteeDetailsDomain {

  implicit val userAnswersReader: UserAnswersReader[GuaranteeDetailsDomain] =
    GuaranteeDetailsSection.reader
      .map(_.value.toList)
      .flatMap {
        case Nil =>
          UserAnswersReader.fail[GuaranteeDetailsDomain](GuaranteeTypePage(Index(0)))
        case x =>
          x.zipWithIndex
            .traverse[UserAnswersReader, GuaranteeDomain] {
              case (_, index) => GuaranteeDomain.userAnswersReader(Index(index))
            }
            .map(GuaranteeDetailsDomain.apply)
      }

}
