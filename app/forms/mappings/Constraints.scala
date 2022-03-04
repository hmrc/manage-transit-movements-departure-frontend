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

package forms.mappings

import models.Index
import models.reference.CountryCode
import play.api.data.validation.{Constraint, Invalid, Valid}

import java.time.LocalDate
import scala.util.matching.Regex

trait Constraints {

  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint {
      input =>
        constraints
          .map(_.apply(input))
          .find(_ != Valid)
          .getOrElse(Valid)
    }

  protected def minimumValue[A](minimum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>
        import ev._

        if (input >= minimum) {
          Valid
        } else {
          Invalid(errorKey, minimum)
        }
    }

  protected def maximumValue[A](maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>
        import ev._

        if (input <= maximum) {
          Valid
        } else {
          Invalid(errorKey, maximum)
        }
    }

  protected def inRange[A](itemIndex: A, minimum: A, maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>
        import ev._

        if (input >= minimum && input <= maximum) {
          Valid
        } else {
          Invalid(errorKey, itemIndex, minimum, maximum)
        }
    }

  protected def regexp(regex: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.matches(regex) =>
        Valid
      case _ =>
        Invalid(errorKey, regex)
    }

  protected def regexp(regex: String, errorKey: String, args: Any*): Constraint[String] =
    Constraint {
      case str if str.matches(regex) =>
        Valid
      case _ =>
        Invalid(errorKey, args: _*)
    }

  protected def maxLength(maximum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, maximum)
    }

  protected def maxLength(maximum: Int, errorKey: String, args: Any*): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, args: _*)
    }

  protected def minGrossMass(minimum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.toDouble > minimum.toDouble =>
        Valid
      case _ =>
        Invalid(errorKey, minimum)
    }

  protected def minGrossMass(minimum: Int, errorKey: String, args: Any*): Constraint[String] =
    Constraint {
      case str if str.toDouble > minimum.toDouble =>
        Valid
      case _ =>
        Invalid(errorKey, args: _*)
    }

  protected def exactLength(exact: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length == exact =>
        Valid
      case _ =>
        Invalid(errorKey, exact)
    }

  protected def maxDate(maximum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isAfter(maximum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def minDate(minimum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isBefore(minimum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def nonEmptySet(errorKey: String): Constraint[Set[_]] =
    Constraint {
      case set if set.nonEmpty =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  protected def regexp(regex: Regex, errorKey: String, args: Seq[Any]): Constraint[String] =
    Constraint {
      case str if str.matches(regex.pattern.pattern()) =>
        Valid
      case _ =>
        Invalid(errorKey, args: _*)
    }

  protected def regexp(regex: Regex, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.matches(regex.pattern.pattern()) =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  protected def minLength(minimum: Int, errorKey: String, args: Any*): Constraint[String] =
    Constraint {
      case str if str.length >= minimum =>
        Valid
      case _ =>
        Invalid(errorKey, args: _*)
    }

  protected def isSimplified(simplified: Boolean, countryCode: CountryCode, errorKey: String): Constraint[String] =
    if (simplified) {
      prefix(countryCode, errorKey)
    } else {
      Constraint {
        case _ => Valid
      }
    }

  private def prefix(countryCode: CountryCode, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.toUpperCase.take(2) == countryCode.code.toUpperCase.take(2) =>
        Valid
      case _ =>
        Invalid(errorKey, countryCode.code)
    }

  protected def doesNotExistIn[A](values: Seq[A], index: Index, errorKey: String, args: Any*)(implicit ev: StringEquivalence[A]): Constraint[String] = {
    import StringEquivalence._

    val valuesFilterWithoutCurrentIndex: Seq[A] =
      values.zipWithIndex.filterNot(_._2 == index.position).map(_._1)
    Constraint {
      x =>
        if (valuesFilterWithoutCurrentIndex.exists(_.equalsString(x))) {
          Invalid(errorKey, args: _*)
        } else {
          Valid
        }

    }
  }
}
