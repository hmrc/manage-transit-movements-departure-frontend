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

package models.journeyDomain.routeDetails.locationOfGoods

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import pages.routeDetails.locationOfGoods.LocationOfGoodsContactNamePage
import pages.traderDetails.holderOfTransit.contact._

class AdditionalContactDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "AdditionalContact" - {

    "can be parsed from UserAnswers" - {

      "when additional contact has a name and telephone number" in {
        val name = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(LocationOfGoodsContactNamePage)(name)

        val expectedResult = AdditionalContactDomain(
          name = name
        )

        val result: EitherType[AdditionalContactDomain] = UserAnswersReader[AdditionalContactDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when additional contact has no name" in {

        val userAnswers = emptyUserAnswers

        val result: EitherType[AdditionalContactDomain] = UserAnswersReader[AdditionalContactDomain].run(userAnswers)

        result.left.value.page mustBe LocationOfGoodsContactNamePage
      }

      //TODO: remove ignore when telephone page is added
      "when additional contact has no telephone number" ignore {
        val name = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(NamePage)(name)

        val result: EitherType[AdditionalContactDomain] = UserAnswersReader[AdditionalContactDomain].run(userAnswers)

        result.left.value.page mustBe TelephoneNumberPage
      }
    }
  }
}
