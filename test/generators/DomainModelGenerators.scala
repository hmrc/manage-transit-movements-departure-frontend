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

package generators

import models.journeyDomain.routeDetails.transit.{OfficeOfTransitDomain, TransitDomain}
import models.reference.{Country, CustomsOffice}
import models.{DateTime, Index}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait DomainModelGenerators {
  self: Generators =>

  implicit lazy val arbitraryOfficeOfTransitDomain: Arbitrary[OfficeOfTransitDomain] = Arbitrary {
    for {
      country       <- Gen.option(arbitrary[Country])
      customsOffice <- arbitrary[CustomsOffice]
      eta           <- Gen.option(arbitrary[DateTime])
      index         <- arbitrary[Int].map(Index(_))
    } yield OfficeOfTransitDomain(country, customsOffice, eta)(index)
  }

  lazy val arbitraryEmptyTransitDomain: Arbitrary[Option[TransitDomain]] = Arbitrary {
    Gen.oneOf(
      None,
      Some(TransitDomain(None, Nil))
    )
  }

  lazy val arbitraryPopulatedTransitDomain: Arbitrary[Option[TransitDomain]] = Arbitrary {
    for {
      isT2DeclarationType <- Gen.option(arbitrary[Boolean])
      officesOfTransit    <- listWithMaxLength[OfficeOfTransitDomain]()
    } yield Some(TransitDomain(isT2DeclarationType, officesOfTransit))
  }
}
