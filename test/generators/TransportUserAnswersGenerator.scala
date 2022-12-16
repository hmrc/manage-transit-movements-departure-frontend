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

package generators

import models.journeyDomain.transport._
import models.{Index, UserAnswers}
import models.journeyDomain.transport.{PreRequisitesDomain, TransportDomain, TransportMeansActiveDomain, TransportMeansDepartureDomain, TransportMeansDomain}
import org.scalacheck.Gen

trait TransportUserAnswersGenerator {
  self: UserAnswersGenerator =>

  def arbitraryTransportAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[TransportDomain](userAnswers)

  def arbitraryPreRequisitesAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[PreRequisitesDomain](userAnswers)

  def arbitraryTransportMeansAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[TransportMeansDomain](userAnswers)

//  def arbitraryTransportMeansActiveAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
//    buildUserAnswers[TransportMeansDomainWithOtherInlandMode](userAnswers)(userAnswersReader(i))

  def arbitraryTransportMeansDepartureAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[TransportMeansDepartureDomain](userAnswers)

  def arbitraryTransportMeansActiveListAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[TransportMeansActiveListDomain](userAnswers)

  def arbitraryTransportMeansActiveAnswers(userAnswers: UserAnswers, index: Index): Gen[UserAnswers] =
    buildUserAnswers[TransportMeansActiveDomain](userAnswers)(TransportMeansActiveDomain.userAnswersReader(index))

  def arbitrarySupplyChainActorAnswers(userAnswers: UserAnswers, index: Index): Gen[UserAnswers] =
    buildUserAnswers[SupplyChainActorDomain](userAnswers)(SupplyChainActorDomain.userAnswersReader(index))
}
