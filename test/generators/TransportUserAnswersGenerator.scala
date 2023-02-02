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

package generators

import models.journeyDomain.transport._
import models.journeyDomain.transport.authorisationsAndLimit.authorisations.AuthorisationDomain
import models.journeyDomain.transport.authorisationsAndLimit.limit.LimitDomain
import models.journeyDomain.transport.equipment.EquipmentDomain
import models.journeyDomain.transport.equipment.index.itemNumber.ItemNumberDomain
import models.journeyDomain.transport.equipment.seal.SealDomain
import models.journeyDomain.transport.supplyChainActors.SupplyChainActorDomain
import models.journeyDomain.transport.transportMeans._
import models.{Index, UserAnswers}
import org.scalacheck.Gen

trait TransportUserAnswersGenerator {
  self: UserAnswersGenerator =>

  def arbitraryTransportAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[TransportDomain](userAnswers)

  def arbitraryPreRequisitesAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[PreRequisitesDomain](userAnswers)

  def arbitraryTransportMeansAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[TransportMeansDomain](userAnswers)

  def arbitraryTransportMeansDepartureAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[TransportMeansDepartureDomain](userAnswers)

  def arbitraryTransportMeansActiveAnswers(userAnswers: UserAnswers, index: Index): Gen[UserAnswers] =
    buildUserAnswers[TransportMeansActiveDomain](userAnswers)(TransportMeansActiveDomain.userAnswersReader(index))

  def arbitrarySupplyChainActorAnswers(userAnswers: UserAnswers, index: Index): Gen[UserAnswers] =
    buildUserAnswers[SupplyChainActorDomain](userAnswers)(SupplyChainActorDomain.userAnswersReader(index))

  def arbitraryAuthorisationAnswers(userAnswers: UserAnswers, index: Index): Gen[UserAnswers] =
    buildUserAnswers[AuthorisationDomain](userAnswers)(AuthorisationDomain.userAnswersReader(index))

  def arbitraryLimitAnswers(userAnswers: UserAnswers): Gen[UserAnswers] =
    buildUserAnswers[LimitDomain](userAnswers)

  def arbitraryEquipmentAnswers(userAnswers: UserAnswers, equipmentIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[EquipmentDomain](userAnswers)(EquipmentDomain.userAnswersReader(equipmentIndex))

  def arbitrarySealAnswers(userAnswers: UserAnswers, equipmentIndex: Index, sealIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[SealDomain](userAnswers)(SealDomain.userAnswersReader(equipmentIndex, sealIndex))

  def arbitraryGoodsItemNumberAnswers(userAnswers: UserAnswers, equipmentIndex: Index, itemNumberIndex: Index): Gen[UserAnswers] =
    buildUserAnswers[ItemNumberDomain](userAnswers)(ItemNumberDomain.userAnswersReader(equipmentIndex, itemNumberIndex))
}
