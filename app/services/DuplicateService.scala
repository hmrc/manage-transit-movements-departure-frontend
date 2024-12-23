/*
 * Copyright 2024 HM Revenue & Customs
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

package services

import connectors.CacheConnector
import models.{LocalReferenceNumber, UserAnswersResponse}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DuplicateService @Inject() (
  cacheConnector: CacheConnector
)(implicit ec: ExecutionContext) {

  def copyUserAnswers(
    oldLocalReferenceNumber: LocalReferenceNumber,
    newLocalReferenceNumber: LocalReferenceNumber
  )(implicit hc: HeaderCarrier): Future[Boolean] =
    cacheConnector.copy(oldLocalReferenceNumber, newLocalReferenceNumber)

  def doesDraftOrSubmissionExistForLrn(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[Boolean] =
    cacheConnector.get(lrn).flatMap {
      case UserAnswersResponse.Answers(_) => Future.successful(true)
      case _                              => doesIE028ExistForLrn(lrn)
    }

  def doesIE028ExistForLrn(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[Boolean] =
    cacheConnector.getMessages(lrn).map(_.contains("IE028"))
}
