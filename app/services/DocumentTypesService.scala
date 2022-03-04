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
import models.DocumentTypeList
import models.reference.DocumentType
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DocumentTypesService @Inject() (referenceDataConnector: ReferenceDataConnector)(implicit ec: ExecutionContext) {

  def getDocumentTypes()(implicit hc: HeaderCarrier): Future[DocumentTypeList] =
    referenceDataConnector
      .getDocumentTypes()
      .map(sort)

  private def sort(documentTypes: Seq[DocumentType]): DocumentTypeList =
    DocumentTypeList(documentTypes.sortBy(_.description.toLowerCase))
}
