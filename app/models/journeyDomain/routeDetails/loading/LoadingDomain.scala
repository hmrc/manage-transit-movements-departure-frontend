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

package models.journeyDomain.routeDetails.loading

import models.UserAnswers
import models.domain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.journeyDomain.{JourneyDomainModel, Stage}
import models.reference.UnLocode
import pages.routeDetails.loading.PlaceOfLoadingAddUnLocodeYesNoPage
import pages.routeDetails.loading.PlaceOfLoadingUnLocodePage
import play.api.mvc.Call

//TODO: Add country and location as params when creating the page
case class LoadingDomain(
  unLocode: Option[UnLocode]
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, stage: Stage): Option[Call] =
    None // TODO - update to CYA once built
}

object LoadingDomain {

  implicit val userAnswersReader: UserAnswersReader[LoadingDomain] =
    //TODO - Add PlaceOfLoadingAddExtraInformationPage when country and location domain built
    PlaceOfLoadingAddUnLocodeYesNoPage.filterOptionalDependent(identity)(PlaceOfLoadingUnLocodePage.reader).map(LoadingDomain.apply)
}
