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

package services

import connectors.ReferenceDataConnector
import models.UnLocodeList
import models.reference.UnLocode
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UnLocodesService @Inject() (
  referenceDataConnector: ReferenceDataConnector
)(implicit ec: ExecutionContext) {

  def getUnLocodes(implicit hc: HeaderCarrier): Future[UnLocodeList] =
    referenceDataConnector.getUnLocodes
      .map(sort)

  private def sort(unLocodeList: UnLocodeList): UnLocodeList =
    sort(unLocodeList.unLocodes)

  private def sort(unLocodes: Seq[UnLocode]): UnLocodeList =
    UnLocodeList(unLocodes.sortBy(_.name.toLowerCase))

}
