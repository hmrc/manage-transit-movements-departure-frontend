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
    arbitraryRepresentativeAnswers
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithEori: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithEori,
    arbitraryRepresentativeAnswers
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithoutEori: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithoutEori,
    arbitraryRepresentativeAnswers
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithTirId: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithTirId,
    arbitraryRepresentativeAnswers
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithoutTirId: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithoutTirId,
    arbitraryRepresentativeAnswers
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithAdditionalContact: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithAdditionalContact,
    arbitraryRepresentativeAnswers
  )

  lazy val arbitraryTraderDetailsAnswersWithHolderOfTransitWithoutAdditionalContact: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswersWithoutAdditionalContact,
    arbitraryRepresentativeAnswers
  )

  lazy val arbitraryTraderDetailsAnswersWithRepresentative: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswers,
    arbitraryRepresentativeAnswersActingAsRepresentative
  )

  lazy val arbitraryTraderDetailsAnswersWithoutRepresentative: Gen[UserAnswers] = combineUserAnswers(
    arbitraryHolderOfTransitAnswers,
    arbitraryRepresentativeAnswersNotActingAsRepresentative
  )
}
