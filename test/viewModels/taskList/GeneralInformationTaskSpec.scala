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

package viewModels.taskList

import base.SpecBase
import generators.Generators
import org.mockito.Mockito.when
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.taskList.TaskStatus._

class GeneralInformationTaskSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "name" - {
    "must be General information" - {
      "when status is CannotStartYet" in {
        
      }
    }
  }

  "id" - {
    "must be general-information" in {
      val task = new GeneralInformationTask(emptyUserAnswers)
      task.id mustBe "general-information"
    }
  }

  "href" - {
    "must be None" - {
      "when status is CannotStartYet" in {
        val task = new GeneralInformationTask(emptyUserAnswers)
        task.status mustBe "general-information"
      }
    }
  }
}
