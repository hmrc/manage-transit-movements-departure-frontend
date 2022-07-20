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
import controllers.guaranteeDetails.{routes => gdRoutes}
import models.DeclarationType.Option4
import models.domain.{UserAnswersReader, _}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.{Index, RichJsArray, UserAnswers}
import pages.guaranteeDetails.guarantee.GuaranteeTypePage
import pages.preTaskList.DeclarationTypePage
import pages.sections.guaranteeDetails.GuaranteeDetailsSection
import play.api.mvc.Call

case class GuaranteeDetailsDomain(
  guarantees: Seq[GuaranteeDomain]
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] =
    userAnswers.get(DeclarationTypePage) map {
      case Option4 => gdRoutes.GuaranteeAddedTIRController.onPageLoad(userAnswers.lrn)
      case _       => gdRoutes.AddAnotherGuaranteeController.onPageLoad(userAnswers.lrn)
    }
}

object GuaranteeDetailsDomain {

  implicit val userAnswersReader: UserAnswersReader[GuaranteeDetailsDomain] =
    GuaranteeDetailsSection.reader.flatMap {
      case x if x.isEmpty =>
        UserAnswersReader.fail[GuaranteeDetailsDomain](GuaranteeTypePage(Index(0)))
      case x =>
        x.traverse[GuaranteeDomain](GuaranteeDomain.userAnswersReader).map(GuaranteeDetailsDomain.apply)
    }
}
