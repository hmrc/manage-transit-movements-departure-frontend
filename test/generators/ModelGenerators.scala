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

import models._
import models.reference._
import org.scalacheck.{Arbitrary, Gen}
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs._

trait ModelGenerators {
  self: Generators =>

  implicit lazy val arbitraryAdditionalDeclarationType: Arbitrary[AdditionalDeclarationType] =
    Arbitrary {
      for {
        code        <- Gen.oneOf("A", "D")
        description <- nonEmptyString
      } yield AdditionalDeclarationType(code, description)
    }

  implicit lazy val arbitrarySubmissionState: Arbitrary[SubmissionState.Value] = Arbitrary {
    Gen.oneOf(SubmissionState.values.toSeq)
  }

  implicit lazy val arbitraryProcedureType: Arbitrary[ProcedureType] =
    Arbitrary {
      Gen.oneOf(ProcedureType.values)
    }

  implicit lazy val arbitraryDeclarationType: Arbitrary[DeclarationType] =
    Arbitrary {
      for {
        code        <- Gen.oneOf("T", "T1", "T2", "T2F", "TIR")
        description <- nonEmptyString
      } yield DeclarationType(code, description)
    }

  lazy val arbitraryNonTIRDeclarationType: Arbitrary[DeclarationType] =
    Arbitrary {
      for {
        code        <- Gen.oneOf("T", "T1", "T2", "T2F")
        description <- nonEmptyString
      } yield DeclarationType(code, description)
    }

  lazy val arbitraryTIRDeclarationType: Arbitrary[DeclarationType] =
    Arbitrary {
      for {
        description <- nonEmptyString
      } yield DeclarationType("TIR", description)
    }

  implicit lazy val arbitrarySecurityDetailsType: Arbitrary[SecurityType] =
    Arbitrary {
      for {
        code        <- Gen.oneOf("0", "1", "2", "3")
        description <- nonEmptyString
      } yield SecurityType(code, description)
    }

  lazy val arbitrarySomeSecurityDetailsType: Arbitrary[SecurityType] =
    Arbitrary {
      for {
        code        <- Gen.oneOf("1", "2", "3")
        description <- nonEmptyString
      } yield SecurityType(code, description)
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
      } yield CustomsOffice(s"XI$id", name, phoneNumber)
    }

  lazy val arbitraryGbCustomsOffice: Arbitrary[CustomsOffice] =
    Arbitrary {
      for {
        id          <- stringsWithMaxLength(stringMaxLength)
        name        <- stringsWithMaxLength(stringMaxLength)
        phoneNumber <- Gen.option(stringsWithMaxLength(stringMaxLength))
      } yield CustomsOffice(s"GB$id", name, phoneNumber)
    }

  lazy val arbitraryOfficeOfDeparture: Arbitrary[CustomsOffice] =
    Arbitrary {
      Gen.oneOf(arbitraryGbCustomsOffice.arbitrary, arbitraryXiCustomsOffice.arbitrary)
    }

  implicit def arbitrarySelectableList[T <: Selectable](implicit arbitrary: Arbitrary[T]): Arbitrary[SelectableList[T]] = Arbitrary {
    for {
      values <- listWithMaxLength[T]()
    } yield SelectableList(values.distinctBy(_.value))
  }

  implicit def arbitraryRadioableList[T <: Radioable[T]](implicit arbitrary: Arbitrary[T]): Arbitrary[Seq[T]] = Arbitrary {
    for {
      values <- listWithMaxLength[T]()
    } yield values.distinctBy(_.code)
  }

  implicit lazy val arbitraryMode: Arbitrary[Mode] = Arbitrary {
    Gen.oneOf(NormalMode, CheckMode)
  }

  implicit lazy val arbitraryIndex: Arbitrary[Index] = Arbitrary {
    for {
      position <- Gen.choose(0: Int, 10: Int)
    } yield Index(position)
  }

  implicit lazy val arbitraryCall: Arbitrary[Call] = Arbitrary {
    for {
      method <- Gen.oneOf(GET, POST)
      url    <- nonEmptyString
    } yield Call(method, url)
  }

}
