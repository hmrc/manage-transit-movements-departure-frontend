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

import cats.data.NonEmptyList
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen, Shrink}

import java.time._

// TODO: Move away from mixing style to using objects
trait Generators extends UserAnswersGenerator with ModelGenerators {

  lazy val stringMaxLength = 36

  require(stringMaxLength > 1, "Value for `stringMaxLength` must be greater than 1")

  implicit val dontShrink: Shrink[String] = Shrink.shrinkAny

  def genNumberString: Gen[String] = arbitrary[Int].map(_.toString)

  def genIntersperseString(gen: Gen[String], value: String, frequencyV: Int = 1, frequencyN: Int = 10): Gen[String] = {

    val genValue: Gen[Option[String]] = Gen.frequency(frequencyN -> None, frequencyV -> Gen.const(Some(value)))

    for {
      seq1 <- gen
      seq2 <- Gen.listOfN(seq1.length, genValue)
    } yield seq1.toSeq.zip(seq2).foldRight("") {
      case ((n, Some(v)), m) =>
        m + n + v
      case ((n, _), m) =>
        m + n
    }
  }

  def intsInRangeWithCommas(min: Int, max: Int): Gen[String] = {
    val numberGen = choose[Int](min, max)
    genIntersperseString(numberGen.toString, ",")
  }

  def intsLargerThanMaxValue: Gen[BigInt] =
    arbitrary[BigInt] retryUntil (
      x => x > Int.MaxValue
    )

  def intsSmallerThanMinValue: Gen[BigInt] =
    arbitrary[BigInt] retryUntil (
      x => x < Int.MinValue
    )

  def nonNumerics: Gen[String] =
    alphaStr suchThat (_.nonEmpty)

  def decimals: Gen[String] =
    arbitrary[BigDecimal]
      .retryUntil(_.abs < Int.MaxValue)
      .retryUntil(!_.isValidInt)
      .map(_.formatted("%f"))

  def decimalsPositive: Gen[String] =
    arbitrary[BigDecimal]
      .retryUntil(
        x => x.signum >= 0
      )
      .retryUntil(
        x => x.abs <= Int.MaxValue
      )
      .retryUntil(!_.isValidInt)
      .map(_.formatted("%f"))

  def intsBelowValue(value: Int): Gen[Int] =
    arbitrary[Int] retryUntil (_ < value)

  def intsAboveValue(value: Int): Gen[Int] =
    arbitrary[Int] retryUntil (_ > value)

  def intsOutsideRange(min: Int, max: Int): Gen[Int] =
    arbitrary[Int] retryUntil (
      x => x < min || x > max
    )

  def nonBooleans: Gen[String] =
    nonEmptyString
      .retryUntil(_ != "true")
      .retryUntil(_ != "false")

  def nonEmptyString: Gen[String] =
    for {
      length <- choose(1, stringMaxLength)
      chars  <- listOfN(length, arbitrary[Char])
    } yield chars.mkString

  def stringsWithMaxLength(maxLength: Int, characters: Gen[Char]): Gen[String] =
    for {
      length <- choose(1, maxLength)
      chars  <- listOfN(length, characters)
    } yield chars.mkString

  def stringsWithMaxLength(maxLength: Int): Gen[String] =
    for {
      length <- choose(1, maxLength)
      chars  <- listOfN(length, arbitrary[Char])
    } yield chars.mkString

  def alphaStringsWithMaxLength(maxLength: Int): Gen[String] =
    for {
      length <- choose(1, maxLength)
      chars  <- listOfN(length, Gen.alphaNumChar)
    } yield chars.mkString

  def stringsWithLength(length: Int): Gen[String] =
    for {
      chars <- listOfN(length, arbitrary[Char])
    } yield chars.mkString

  def alphaNumericWithMaxLength(maxLength: Int): Gen[String] =
    for {
      length <- choose(1, maxLength)
      chars  <- listOfN(length, Gen.alphaNumChar)
    } yield chars.mkString

  def extendedAsciiChar: Gen[Char] = chooseNum(128, 254).map(_.toChar)

  def extendedAsciiWithMaxLength(maxLength: Int): Gen[String] =
    for {
      length <- choose(1, maxLength)
      chars  <- listOfN(length, extendedAsciiChar)
    } yield chars.mkString

  def stringsLongerThan(minLength: Int, withOnlyPrintableAscii: Boolean = false): Gen[String] =
    for {
      maxLength     <- (minLength * 2).max(100)
      length        <- Gen.chooseNum(minLength + 1, maxLength)
      extendedAscii <- extendedAsciiChar
      chars <- {
        if (withOnlyPrintableAscii) {
          listOfN(length, Gen.alphaChar)
        } else {
          val listOfChar = listOfN(length, arbitrary[Char])
          listOfChar.map(_ ++ List(extendedAscii))
        }
      }
    } yield chars.mkString

  def stringsExceptSpecificValues(excluded: Seq[String]): Gen[String] =
    nonEmptyString retryUntil (!excluded.contains(_))

  def oneOf[T](xs: Seq[Gen[T]]): Gen[T] =
    if (xs.isEmpty) {
      throw new IllegalArgumentException("oneOf called on empty collection")
    } else {
      val vector = xs.toVector
      choose(0, vector.size - 1).flatMap(vector(_))
    }

  def datesBetween(min: LocalDate, max: LocalDate): Gen[LocalDate] = {

    def toMillis(date: LocalDate): Long =
      date.atStartOfDay.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map {
      millis =>
        Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate
    }
  }

  def dateTimesBetween(min: LocalDateTime, max: LocalDateTime): Gen[LocalDateTime] = {

    def toMillis(date: LocalDateTime): Long =
      date.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map {
      millis =>
        Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDateTime
    }
  }

  def nonEmptyListOf[A](maxLength: Int)(implicit a: Arbitrary[A]): Gen[NonEmptyList[A]] =
    listWithMaxLength[A](maxLength).map(NonEmptyList.fromListUnsafe _)

  def listWithMaxLength[A](maxLength: Int)(implicit a: Arbitrary[A]): Gen[List[A]] =
    for {
      length <- choose(1, maxLength)
      seq    <- listOfN(length, arbitrary[A])
    } yield seq

  def listWithMaxLength[T](maxSize: Int, gen: Gen[T]): Gen[Seq[T]] =
    for {
      size  <- Gen.choose(0, maxSize)
      items <- Gen.listOfN(size, gen)
    } yield items

  def nonEmptyListWithMaxSize[T](maxSize: Int, gen: Gen[T]): Gen[NonEmptyList[T]] =
    for {
      head     <- gen
      tailSize <- Gen.choose(1, maxSize - 1)
      tail     <- Gen.listOfN(tailSize, gen)
    } yield NonEmptyList(head, tail)

  implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
    datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
  }

  implicit lazy val arbitraryLocalTime: Arbitrary[LocalTime] = Arbitrary {
    dateTimesBetween(
      LocalDateTime.of(1900, 1, 1, 0, 0, 0),
      LocalDateTime.of(2100, 1, 1, 0, 0, 0)
    ).map(_.toLocalTime)
  }

  implicit lazy val arbitraryLocalDateTime: Arbitrary[LocalDateTime] = Arbitrary {
    dateTimesBetween(
      LocalDateTime.of(1900, 1, 1, 0, 0, 0),
      LocalDateTime.of(2100, 1, 1, 0, 0, 0)
    ).map(
      x => x.withNano(0).withSecond(0)
    )
  }

  lazy val genExemptNationalityCode: Gen[Int] =
    for {
      range1    <- Gen.chooseNum(20, 29)
      range2    <- Gen.chooseNum(50, 59)
      range3    <- Gen.chooseNum(70, 79)
      range4    <- Gen.oneOf(Seq(2, 5, 7))
      pickRange <- Gen.oneOf(range1, range2, range3, range4)
    } yield pickRange

  val localDateGen: Gen[LocalDate] = datesBetween(LocalDate.of(1900, 1, 1), LocalDate.now)
}
