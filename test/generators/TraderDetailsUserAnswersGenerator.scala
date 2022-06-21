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

import models.UserAnswers
import org.scalacheck.Gen

trait TraderDetailsUserAnswersGenerator
    extends HolderOfTransitUserAnswersGenerator
    with RepresentativeUserAnswersGenerator
    with TraderDetailsConsignmentAnswersGenerator {
  self: Generators =>

  lazy val arbitraryTraderDetailsAnswers: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswers,
    arbitraryRepresentativeAnswers,
    arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithEori: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithEori,
    arbitraryRepresentativeAnswers,
    arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithoutEori: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithoutEori,
    arbitraryRepresentativeAnswers,
    arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithTirId: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithTirId,
    arbitraryRepresentativeAnswers,
    arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithoutTirId: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithoutTirId,
    arbitraryRepresentativeAnswers,
    arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithAdditionalContact: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithAdditionalContact,
    arbitraryRepresentativeAnswers,
    arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithoutAdditionalContact: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithoutAdditionalContact,
    arbitraryRepresentativeAnswers,
    arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee
  )

  lazy val arbitraryTraderDetailsAnswersWithRepresentative: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswers,
    arbitraryRepresentativeAnswersActingAsRepresentative,
    arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee
  )

  lazy val arbitraryTraderDetailsAnswersWithoutRepresentative: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswers,
    arbitraryRepresentativeAnswersNotActingAsRepresentative,
    arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee
  )

  lazy val arbitraryTraderDetailsWithConsignorAndConsigneeAnswers: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswers,
    arbitraryRepresentativeAnswers,
    arbitraryTraderDetailsConsignmentAnswersWithConsignorAndConsignee
  )

}
