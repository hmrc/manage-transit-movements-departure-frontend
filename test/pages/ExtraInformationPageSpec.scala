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

package pages

import models.Index
import pages.addItems.ExtraInformationPage
import pages.behaviours.PageBehaviours

class ExtraInformationPageSpec extends PageBehaviours {

  private val index = Index(0)

  "ExtraInformationPage" - {

    beRetrievable[String](ExtraInformationPage(index, index))

    beSettable[String](ExtraInformationPage(index, index))

    beRemovable[String](ExtraInformationPage(index, index))
  }
}
