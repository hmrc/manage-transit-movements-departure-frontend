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

package viewModels.routeDetails.loadingAndUnloading

import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.routeDetails.loadingAndUnloading.LoadingAndUnloadingCheckYourAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class LoadingAndUnloadingAnswersViewModel(sections: Seq[Section])

object LoadingAndUnloadingAnswersViewModel {

  class LoadingAndUnloadingAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): LoadingAndUnloadingAnswersViewModel = {
      val helper = new LoadingAndUnloadingCheckYourAnswersHelper(userAnswers, mode)

      val loadingSection = Section(
        sectionTitle = messages("routeDetails.loadingAndUnloading.checkYourAnswers.loading"),
        rows = Seq(
          helper.addLoadingUnLocode,
          helper.loadingUnLocode,
          helper.addLoadingCountryAndLocation,
          helper.loadingCountry,
          helper.loadingLocation
        ).flatten
      )

      val unloadingSection = Section(
        sectionTitle = messages("routeDetails.loadingAndUnloading.checkYourAnswers.unloading"),
        rows = Seq(
          helper.addPlaceOfUnloading,
          helper.addUnloadingUnLocode,
          helper.unloadingUnLocode,
          helper.addUnloadingCountryAndLocation,
          helper.unloadingCountry,
          helper.unloadingLocation
        ).flatten
      )

      new LoadingAndUnloadingAnswersViewModel(Seq(loadingSection, unloadingSection))
    }
  }
}
