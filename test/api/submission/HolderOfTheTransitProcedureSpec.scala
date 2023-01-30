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

package api.submission

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generated._
import generators.Generators
import models.UserAnswers
import models.domain.UserAnswersReader
import models.journeyDomain.DepartureDomain
import models.journeyDomain.DepartureDomain.userAnswersReader
import models.journeyDomain.traderDetails.holderOfTransit.HolderOfTransitDomain

class HolderOfTheTransitProcedureSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  val uA: UserAnswers = arbitraryDepartureAnswers(emptyUserAnswers).sample.value

  "HolderOfTheTransitProcedure" - {

    "transform is called" - {

      "will convert to API format" in {

        UserAnswersReader[DepartureDomain](userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)).run(uA).map {
          case DepartureDomain(_, traderDetails, _, _, _) =>
            val expected: HolderOfTheTransitProcedureType14 = traderDetails.holderOfTransit match {
              case HolderOfTransitDomain.HolderOfTransitEori(eori, name, country, address, additionalContact) =>
                HolderOfTheTransitProcedureType14(
                  identificationNumber = eori.map(
                    x => x.value
                  ),
                  TIRHolderIdentificationNumber = None,
                  name = Some(name),
                  Address = Some(AddressType17(address.numberAndStreet, address.postalCode, address.city, country.code.code)),
                  ContactPerson = additionalContact.map(
                    x => ContactPersonType05(x.name, x.telephoneNumber, None)
                  )
                )
              case HolderOfTransitDomain.HolderOfTransitTIR(tir, name, country, address, additionalContact) =>
                HolderOfTheTransitProcedureType14(
                  identificationNumber = tir,
                  TIRHolderIdentificationNumber = None,
                  name = Some(name),
                  Address = Some(AddressType17(address.numberAndStreet, address.postalCode, address.city, country.code.code)),
                  ContactPerson = additionalContact.map(
                    x => ContactPersonType05(x.name, x.telephoneNumber, None)
                  )
                )
            }

            val converted: HolderOfTheTransitProcedureType14 = HolderOfTheTransitProcedure.transform(traderDetails)

            converted mustBe expected
        }

      }

    }

  }
}
