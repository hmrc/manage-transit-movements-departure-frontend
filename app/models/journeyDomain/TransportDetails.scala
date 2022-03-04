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

package models.journeyDomain

import cats.implicits._
import models.journeyDomain.TransportDetails._
import models.reference.CountryCode
import pages._

case class TransportDetails(
  inlandMode: InlandMode,
  detailsAtBorder: DetailsAtBorder
)

object TransportDetails {

  implicit val userAnswersParser: UserAnswersReader[TransportDetails] =
    (
      UserAnswersReader[InlandMode],
      UserAnswersReader[DetailsAtBorder]
    ).tupled.map((TransportDetails.apply _).tupled)

  sealed trait InlandMode {
    def code: Int
  }

  object InlandMode {

    object Constants {
      val codesSingleDigit: Seq[Int] = Rail.Constants.codesSingleDigit ++ Mode5or7.Constants.codesSingleDigit
      val codesDoubleDigit: Seq[Int] = Rail.Constants.codesDoubleDigit ++ Mode5or7.Constants.codesDoubleDigit
      val codes: Seq[Int]            = codesSingleDigit ++ codesDoubleDigit
    }

    implicit val userAnswersReader: UserAnswersReader[InlandMode] =
      InlandModePage.reader.flatMap {
        case code if Rail.Constants.codes.contains(code.toInt)     => UserAnswersReader[Rail].widen[InlandMode]
        case code if Mode5or7.Constants.codes.contains(code.toInt) => UserAnswersReader[Mode5or7].widen[InlandMode]
        case _                                                     => UserAnswersReader[NonSpecialMode].widen[InlandMode]
      }

    final case class Rail(code: Int, departureId: Option[String]) extends InlandMode

    object Rail {

      object Constants {
        val codesSingleDigit: Seq[Int] = Seq(2)
        val codesDoubleDigit: Seq[Int] = Seq(20)
        val codes: Seq[Int]            = codesSingleDigit ++ codesDoubleDigit
      }

      implicit val userAnswersReaderRail: UserAnswersReader[Rail] =
        InlandModePage.reader.flatMap {
          code =>
            AddIdAtDeparturePage.optionalReader
              .flatMap {
                case Some(false) => Rail(code.toInt, None).pure[UserAnswersReader]
                case _ =>
                  IdAtDeparturePage.reader.map(
                    x => Rail(code.toInt, Some(x))
                  )
              }
        }
    }

    final case class Mode5or7(code: Int) extends InlandMode

    case object Mode5or7 {

      object Constants {
        val codesSingleDigit: Seq[Int] = Seq(5, 7)
        val codesDoubleDigit: Seq[Int] = Seq(50, 70)
        val codes: Seq[Int]            = codesSingleDigit ++ codesDoubleDigit
      }

      implicit val userAnswersReaderMode5or7: UserAnswersReader[Mode5or7] =
        InlandModePage.reader.flatMap {
          code =>
            Mode5or7(code.toInt).pure[UserAnswersReader]
        }
    }

    final case class NonSpecialMode(code: Int, nationalityAtDeparture: Option[CountryCode], departureId: Option[String]) extends InlandMode

    object NonSpecialMode {

      implicit val userAnswersReaderNonSpecialMode: UserAnswersReader[NonSpecialMode] =
        (
          InlandModePage.reader.map(_.toInt),
          NationalityAtDeparturePage.optionalReader,
          IdAtDeparturePage.optionalReader
        ).tupled.map((NonSpecialMode.apply _).tupled)
    }
  }

  sealed trait DetailsAtBorder

  object DetailsAtBorder {

    implicit val reader: UserAnswersReader[DetailsAtBorder] =
      ChangeAtBorderPage.reader.flatMap {
        case true  => UserAnswersReader[NewDetailsAtBorder].widen[DetailsAtBorder]
        case false => UserAnswersReader[SameDetailsAtBorder.type].widen[DetailsAtBorder]
      }

    object SameDetailsAtBorder extends DetailsAtBorder {
      implicit val userAnswersReader: UserAnswersReader[SameDetailsAtBorder.type] = SameDetailsAtBorder.pure[UserAnswersReader]
    }

    final case class NewDetailsAtBorder(
      mode: String,
      modeCrossingBorder: ModeCrossingBorder
    ) extends DetailsAtBorder

    object NewDetailsAtBorder {

      implicit val userAnswersReader: UserAnswersReader[NewDetailsAtBorder] =
        (
          ModeAtBorderPage.reader,
          UserAnswersReader[ModeCrossingBorder]
        ).tupled.map((NewDetailsAtBorder.apply _).tupled)
    }
  }

  sealed trait ModeCrossingBorder {
    def modeCode: Int
  }

  object ModeCrossingBorder {

    def isExemptFromNationality(mode: String): Boolean = Seq("2", "5", "7").contains(mode.take(1))

    implicit val reader: UserAnswersReader[ModeCrossingBorder] =
      ModeCrossingBorderPage.reader
        .map(_.toInt)
        .flatMap(
          modeCode =>
            if (ModeCrossingBorder.isExemptFromNationality(modeCode.toString)) {
              ModeExemptNationality(modeCode).pure[UserAnswersReader].widen[ModeCrossingBorder]
            } else {
              (
                NationalityCrossingBorderPage.reader,
                IdCrossingBorderPage.reader
              ).tupled.map {
                case (nationality, id) => ModeWithNationality(nationality, modeCode, id)
              }
            }
        )
    final case class ModeExemptNationality(modeCode: Int) extends ModeCrossingBorder
    final case class ModeWithNationality(nationalityCrossingBorder: CountryCode, modeCode: Int, idCrossing: String) extends ModeCrossingBorder
  }

}
