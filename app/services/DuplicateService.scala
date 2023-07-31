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

package services

import connectors.CacheConnector
import forms.NewLocalReferenceNumberFormProvider
import models.SubmissionState.RejectedPendingChanges
import models.{LocalReferenceNumber, UserAnswers}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DuplicateService @Inject() (
  cacheConnector: CacheConnector,
  formProvider: NewLocalReferenceNumberFormProvider
)(implicit ec: ExecutionContext) {

  def copyUserAnswers(
    oldLocalReferenceNumber: LocalReferenceNumber,
    newLocalReferenceNumber: LocalReferenceNumber
  )(implicit hc: HeaderCarrier): Future[Boolean] = cacheConnector.get(oldLocalReferenceNumber) flatMap {
    case Some(userAnswers) =>
      val updatedUserAnswers: UserAnswers =
        userAnswers.copy(lrn = newLocalReferenceNumber, isSubmitted = Some(RejectedPendingChanges))
      cacheConnector
        .post(
          updatedUserAnswers
        )
        .flatMap {
          case true  => updateResubmittedLrn(newLocalReferenceNumber, userAnswers)
          case false => Future.successful(false) //TODO refactor this
        }

    // TODO CTCP-3469 Will have to keep any draft declaration with same LRN, can probably handle this when the doesDraftOrSubmissionExistForLrn is called in the backend
    case None => Future.successful(false)
  }

  def updateResubmittedLrn(
    newLocalReferenceNumber: LocalReferenceNumber,
    oldUserAnswers: UserAnswers
  )(implicit hc: HeaderCarrier): Future[Boolean] = {
    val updatedUserAnswers: UserAnswers = oldUserAnswers.copy(resubmittedLrn = Some(newLocalReferenceNumber), isSubmitted = Some(RejectedPendingChanges))
    cacheConnector.post(
      updatedUserAnswers
    )
  }

  def doesDraftOrSubmissionExistForLrn(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[Boolean] =
    cacheConnector.doesDraftOrSubmissionExistForLrn(lrn)

  def doesSubmissionExistForLrn(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[Boolean] =
    cacheConnector.doesSubmissionExistForLrn(lrn)

  def alreadyExists(submittedValue: Option[LocalReferenceNumber])(implicit hc: HeaderCarrier): Future[Boolean] =
    submittedValue match {
      case Some(newLocalReferenceNumber) => doesDraftOrSubmissionExistForLrn(newLocalReferenceNumber)
      case None                          => Future.successful(false)
    }
}
