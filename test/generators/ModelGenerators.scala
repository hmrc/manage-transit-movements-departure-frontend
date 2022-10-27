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

package generators

import models.AddressLine.{Country => _, _}
import models.GuaranteeType._
import models._
import models.domain.StringFieldRegex.{coordinatesLatitudeMaxRegex, coordinatesLongitudeMaxRegex}
import models.reference._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import wolfendale.scalacheck.regexp.RegexpGen

trait ModelGenerators {
  self: Generators =>

  implicit lazy val arbitraryIdentification: Arbitrary[models.transport.transportMeans.departure.Identification] =
    Arbitrary {
      Gen.oneOf(models.transport.transportMeans.departure.Identification.values)
    }

  implicit lazy val arbitraryInlandMode: Arbitrary[InlandMode] =
    Arbitrary {
      Gen.oneOf(models.InlandMode.values)
    }

  implicit lazy val arbitraryLocationType: Arbitrary[models.LocationType] =
    Arbitrary {
      Gen.oneOf(models.LocationType.values)
    }

  implicit lazy val arbitraryLocationOfGoodsIdentification: Arbitrary[LocationOfGoodsIdentification] =
    Arbitrary {
      Gen.oneOf(LocationOfGoodsIdentification.values)
    }

  implicit lazy val arbitraryGuaranteeType: Arbitrary[GuaranteeType] =
    Arbitrary {
      Gen.oneOf(GuaranteeType.values)
    }

  lazy val arbitraryNonOption4GuaranteeType: Arbitrary[GuaranteeType] =
    Arbitrary {
      Gen.oneOf(GuaranteeType.values.filterNot(_ == TIRGuarantee))
    }

  lazy val arbitraryNonOption3Or8GuaranteeType: Arbitrary[GuaranteeType] =
    Arbitrary {
      Gen.oneOf(
        GuaranteeType.values
          .filterNot(_ == CashDepositGuarantee)
          .filterNot(_ == GuaranteeNotRequiredExemptPublicBody)
      )
    }

  lazy val arbitrary012459GuaranteeType: Arbitrary[GuaranteeType] =
    Arbitrary {
      Gen.oneOf(
        GuaranteeWaiver,
        ComprehensiveGuarantee,
        IndividualGuarantee,
        FlatRateVoucher,
        GuaranteeWaiverSecured,
        IndividualGuaranteeMultiple
      )
    }

  lazy val arbitrary01249GuaranteeType: Arbitrary[GuaranteeType] =
    Arbitrary {
      Gen.oneOf(
        GuaranteeWaiver,
        ComprehensiveGuarantee,
        IndividualGuarantee,
        FlatRateVoucher,
        IndividualGuaranteeMultiple
      )
    }

  lazy val arbitrary01234589GuaranteeType: Arbitrary[GuaranteeType] =
    Arbitrary {
      Gen.oneOf(
        GuaranteeWaiver,
        ComprehensiveGuarantee,
        IndividualGuarantee,
        CashDepositGuarantee,
        FlatRateVoucher,
        GuaranteeWaiverSecured,
        GuaranteeNotRequiredExemptPublicBody,
        IndividualGuaranteeMultiple
      )
    }

  lazy val arbitraryARGuaranteeType: Arbitrary[GuaranteeType] =
    Arbitrary {
      Gen.oneOf(
        GuaranteeWaiverByAgreement,
        GuaranteeNotRequired
      )
    }

  implicit lazy val arbitraryRepresentativeCapacity: Arbitrary[models.traderDetails.representative.RepresentativeCapacity] =
    Arbitrary {
      Gen.oneOf(models.traderDetails.representative.RepresentativeCapacity.values)
    }

  implicit lazy val arbitraryCountryCode: Arbitrary[CountryCode] =
    Arbitrary {
      Gen
        .pick(CountryCode.Constants.countryCodeLength, 'A' to 'Z')
        .map(
          code => CountryCode(code.mkString)
        )
    }

  implicit lazy val arbitraryCountry: Arbitrary[Country] =
    Arbitrary {
      for {
        code <- arbitrary[CountryCode]
        name <- nonEmptyString
      } yield Country(code, name)
    }

  implicit lazy val arbitraryProcedureType: Arbitrary[ProcedureType] =
    Arbitrary {
      Gen.oneOf(ProcedureType.values)
    }

  implicit lazy val arbitraryDeclarationType: Arbitrary[DeclarationType] =
    Arbitrary {
      Gen.oneOf(DeclarationType.values)
    }

  lazy val arbitraryNonOption4DeclarationType: Arbitrary[DeclarationType] =
    Arbitrary {
      Gen.oneOf(DeclarationType.values.filterNot(_ == DeclarationType.Option4))
    }

  implicit lazy val arbitrarySecurityDetailsType: Arbitrary[SecurityDetailsType] =
    Arbitrary {
      Gen.oneOf(SecurityDetailsType.values)
    }

  lazy val arbitrarySomeSecurityDetailsType: Arbitrary[SecurityDetailsType] =
    Arbitrary {
      Gen.oneOf(SecurityDetailsType.values.filterNot(_ == SecurityDetailsType.NoSecurityDetails))
    }

  implicit lazy val arbitraryLocalReferenceNumber: Arbitrary[LocalReferenceNumber] =
    Arbitrary {
      for {
        lrn <- stringsWithMaxLength(22: Int, Gen.alphaNumChar)
      } yield new LocalReferenceNumber(lrn)
    }

  implicit lazy val arbitraryEoriNumber: Arbitrary[EoriNumber] =
    Arbitrary {
      for {
        number <- stringsWithMaxLength(17: Int)
      } yield EoriNumber(number)
    }

  implicit lazy val arbitraryCustomsOffice: Arbitrary[CustomsOffice] =
    Arbitrary {
      for {
        id          <- nonEmptyString
        name        <- nonEmptyString
        phoneNumber <- Gen.option(Gen.alphaNumStr)
      } yield CustomsOffice(id, name, phoneNumber)
    }

  lazy val arbitraryXiCustomsOffice: Arbitrary[CustomsOffice] =
    Arbitrary {
      for {
        id          <- stringsWithMaxLength(stringMaxLength)
        name        <- stringsWithMaxLength(stringMaxLength)
        phoneNumber <- Gen.option(stringsWithMaxLength(stringMaxLength))
      } yield CustomsOffice(id, name, phoneNumber)
    }

  lazy val arbitraryGbCustomsOffice: Arbitrary[CustomsOffice] =
    Arbitrary {
      for {
        id          <- stringsWithMaxLength(stringMaxLength)
        name        <- stringsWithMaxLength(stringMaxLength)
        phoneNumber <- Gen.option(stringsWithMaxLength(stringMaxLength))
      } yield CustomsOffice(id, name, phoneNumber)
    }

  lazy val arbitraryOfficeOfDeparture: Arbitrary[CustomsOffice] =
    Arbitrary {
      Gen.oneOf(arbitraryGbCustomsOffice.arbitrary, arbitraryXiCustomsOffice.arbitrary)
    }

  implicit lazy val arbitraryCustomsOfficeList: Arbitrary[CustomsOfficeList] =
    Arbitrary {
      for {
        customsOffices <- listWithMaxLength[CustomsOffice]()
      } yield CustomsOfficeList(customsOffices)
    }

  implicit lazy val arbitraryDynamicAddress: Arbitrary[DynamicAddress] =
    Arbitrary {
      for {
        numberAndStreet <- stringsWithMaxLength(NumberAndStreet.length, Gen.alphaNumChar)
        city            <- stringsWithMaxLength(City.length, Gen.alphaNumChar)
        postalCode      <- Gen.option(stringsWithMaxLength(PostalCode.length, Gen.alphaNumChar))
      } yield DynamicAddress(numberAndStreet, city, postalCode)
    }

  lazy val arbitraryDynamicAddressWithRequiredPostalCode: Arbitrary[DynamicAddress] =
    Arbitrary {
      for {
        numberAndStreet <- stringsWithMaxLength(NumberAndStreet.length, Gen.alphaNumChar)
        city            <- stringsWithMaxLength(City.length, Gen.alphaNumChar)
        postalCode      <- stringsWithMaxLength(PostalCode.length, Gen.alphaNumChar)
      } yield DynamicAddress(numberAndStreet, city, Some(postalCode))
    }

  implicit lazy val arbitraryPostalCodeAddress: Arbitrary[PostalCodeAddress] =
    Arbitrary {
      for {
        streetNumber <- stringsWithMaxLength(StreetNumber.length, Gen.alphaNumChar)
        postalCode   <- stringsWithMaxLength(PostalCode.length, Gen.alphaNumChar)
        country      <- arbitrary[Country]
      } yield PostalCodeAddress(streetNumber, postalCode, country)
    }

  implicit lazy val arbitraryMode: Arbitrary[Mode] = Arbitrary {
    Gen.oneOf(NormalMode, CheckMode)
  }

  implicit lazy val arbitraryCountryList: Arbitrary[CountryList] = Arbitrary {
    for {
      countries <- listWithMaxLength[Country]()
    } yield CountryList(countries)
  }

  implicit lazy val arbitraryIndex: Arbitrary[Index] = Arbitrary {
    for {
      position <- Gen.choose(0: Int, 10: Int)
    } yield Index(position)
  }

  implicit lazy val arbitraryCoordinates: Arbitrary[Coordinates] =
    Arbitrary {
      for {
        latitude  <- RegexpGen.from(coordinatesLatitudeMaxRegex)
        longitude <- RegexpGen.from(coordinatesLongitudeMaxRegex)
      } yield Coordinates(latitude, longitude)
    }

  implicit lazy val arbitraryUnLocode: Arbitrary[UnLocode] =
    Arbitrary {
      for {
        unLocodeExtendedCode <- nonEmptyString
        name                 <- nonEmptyString
      } yield UnLocode(unLocodeExtendedCode, name)
    }

}
