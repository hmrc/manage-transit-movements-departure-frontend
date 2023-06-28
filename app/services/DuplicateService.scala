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
import models.{EoriNumber, LocalReferenceNumber, UserAnswers}
import play.api.data.Form
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DuplicateService @Inject() (
  cacheConnector: CacheConnector,
  formProvider: NewLocalReferenceNumberFormProvider
)(implicit ec: ExecutionContext) {

  def copyUserAnswers(
    oldLocalReferenceNumber: LocalReferenceNumber,
    newLocalReferenceNumber: LocalReferenceNumber,
    eoriNumber: EoriNumber
  )(implicit hc: HeaderCarrier): Future[Boolean] = cacheConnector.get(oldLocalReferenceNumber) flatMap {
    case Some(userAnswers) =>
      val updatedUserAnswers: UserAnswers = userAnswers.copy(lrn = newLocalReferenceNumber, eoriNumber = eoriNumber)
      cacheConnector.post(updatedUserAnswers)
    case None => Future.successful(false)
  }

  def isDuplicateLRN(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[Boolean] =
    cacheConnector.isDuplicateLRN(lrn)

  def populateForm(submittedValue: Option[LocalReferenceNumber])(implicit hc: HeaderCarrier): Future[Form[LocalReferenceNumber]] =
    submittedValue match {
      case Some(newLocalReferenceNumber) => isDuplicateLRN(newLocalReferenceNumber).map(formProvider(_))
      case None                          => Future.successful(formProvider(alreadyExists = false))
    }
}
