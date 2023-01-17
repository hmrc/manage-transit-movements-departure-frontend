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

package models.journeyDomain.transport

import cats.implicits.{catsSyntaxApplicativeId, none}
import models.domain.UserAnswersReader
import models.journeyDomain.JourneyDomainModel
import models.transport.authorisations.AuthorisationType

case class AuthorisationsAndLimitDomain(authorisationsDomain: AuthorisationsDomain, limitDomain: Option[LimitDomain]) extends JourneyDomainModel

object AuthorisationsAndLimitDomain {

  def limitReader(authDomain: AuthorisationsDomain): UserAnswersReader[Option[LimitDomain]] =
    authDomain.authorisationsDomain.exists(_.authorisationType == AuthorisationType.ACR) match {
      case true  => UserAnswersReader[LimitDomain].map(Some(_))
      case false => none[LimitDomain].pure[UserAnswersReader]
    }

  implicit val userAnswersReader: UserAnswersReader[AuthorisationsAndLimitDomain] = {
    for {
      authorisations <- UserAnswersReader[AuthorisationsDomain]
      limit          <- limitReader(authorisations)
    } yield AuthorisationsAndLimitDomain(authorisations, limit)
  }
}
