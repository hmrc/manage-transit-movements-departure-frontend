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

package utils

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.routeDetails.routes
import generators.Generators
import models.reference.{Country, CountryCode, CountryOfDispatch, CustomsOffice}
import models.{CheckMode, CountryList, CustomsOfficeList, Mode}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails._
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

import java.time.{LocalDate, LocalDateTime}

class RouteDetailsCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val mode: Mode               = CheckMode
  private val countryCode: CountryCode = CountryCode("COUNTRY CODE")
  private val country: Country         = Country(countryCode, "COUNTRY DESCRIPTION")

  private val customsOffice = CustomsOffice("OFFICE ID", "OFFICE NAME", countryCode, None)

  "RouteDetailsCheckYourAnswersHelper" - {

    "officeOfTransitRow" - {

      "return None" - {

        "AddAnotherTransitOfficePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.officeOfTransitRow(index, CustomsOfficeList(Nil))
          result mustBe None
        }

        "customs office ID not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddAnotherTransitOfficePage(index))(customsOffice.id)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.officeOfTransitRow(index, CustomsOfficeList(Nil))
          result mustBe None
        }
      }

      "return Some(Row)" - {

        "AddAnotherTransitOfficePage defined at index and customs office ID found" - {

          "arrival time unknown" in {

            val answers = emptyUserAnswers
              .unsafeSetVal(AddAnotherTransitOfficePage(index))(customsOffice.id)

            val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.officeOfTransitRow(index, CustomsOfficeList(Seq(customsOffice)))

            val label = lit"${customsOffice.name} (${customsOffice.id})"

            result mustBe Some(
              Row(
                key = Key(label),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.OfficeOfTransitCountryController.onPageLoad(answers.lrn, index, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"change-office-of-transit-${index.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(answers.lrn, index, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"remove-office-of-transit-${index.display}")
                  )
                )
              )
            )
          }

          "arrival time known" in {

            val arrivalDate = arbitrary[LocalDateTime].sample.value
            val formattedArrivalDate =
              Format.dateFormattedDDMMYYYY(LocalDate.of(arrivalDate.getYear, arrivalDate.getMonth, arrivalDate.getDayOfMonth)).toLowerCase

            val answers = emptyUserAnswers
              .unsafeSetVal(AddAnotherTransitOfficePage(index))(customsOffice.id)
              .unsafeSetVal(ArrivalDatesAtOfficePage(index))(arrivalDate)

            val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.officeOfTransitRow(index, CustomsOfficeList(Seq(customsOffice)))

            val label = lit"${customsOffice.name} (${customsOffice.id})"

            result mustBe Some(
              Row(
                key = Key(label),
                value = Value(lit"$formattedArrivalDate"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.OfficeOfTransitCountryController.onPageLoad(answers.lrn, index, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"change-office-of-transit-${index.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(answers.lrn, index, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"remove-office-of-transit-${index.display}")
                  )
                )
              )
            )
          }
        }
      }
    }

    "movementDestinationCountry" - {

      "return None" - {

        "MovementDestinationCountryPage undefined" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.movementDestinationCountry(CountryList(Nil))
          result mustBe None
        }
      }

      "return Some(Row)" - {

        "country name found" in {

          val answers = emptyUserAnswers.unsafeSetVal(MovementDestinationCountryPage)(countryCode)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.movementDestinationCountry(CountryList(Seq(country)))

          val label = msg"movementDestinationCountry.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${country.description}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.MovementDestinationCountryController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-movement-destination-country")
                )
              )
            )
          )
        }

        "country name not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(MovementDestinationCountryPage)(countryCode)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.movementDestinationCountry(CountryList(Nil))

          val label = msg"movementDestinationCountry.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${countryCode.code}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.MovementDestinationCountryController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-movement-destination-country")
                )
              )
            )
          )
        }
      }
    }

    "destinationCountry" - {

      "return None" - {

        "DestinationCountryPage undefined" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.destinationCountry(CountryList(Nil))
          result mustBe None
        }
      }

      "return Some(Row)" - {

        "country name found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DestinationCountryPage)(countryCode)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.destinationCountry(CountryList(Seq(country)))

          val label = msg"destinationCountry.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${country.description}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.DestinationCountryController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-destination-country")
                )
              )
            )
          )
        }

        "country name not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DestinationCountryPage)(countryCode)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.destinationCountry(CountryList(Nil))

          val label = msg"destinationCountry.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${countryCode.code}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.DestinationCountryController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-destination-country")
                )
              )
            )
          )
        }
      }
    }

    "countryOfDispatch" - {

      "return None" - {

        "CountryOfDispatchPage undefined" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.countryOfDispatch(CountryList(Nil))
          result mustBe None
        }
      }

      "return Some(Row)" - {

        val countryOfDispatch: CountryOfDispatch = CountryOfDispatch(countryCode, isNotEu = false)

        "country name found" in {

          val answers = emptyUserAnswers.unsafeSetVal(CountryOfDispatchPage)(countryOfDispatch)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.countryOfDispatch(CountryList(Seq(country)))

          val label = msg"countryOfDispatch.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${country.description}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.CountryOfDispatchController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-country-of-dispatch")
                )
              )
            )
          )
        }

        "country name not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(CountryOfDispatchPage)(countryOfDispatch)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.countryOfDispatch(CountryList(Nil))

          val label = msg"countryOfDispatch.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${countryCode.code}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.CountryOfDispatchController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-country-of-dispatch")
                )
              )
            )
          )
        }
      }
    }

    "destinationOffice" - {

      "return None" - {

        "DestinationOfficePage undefined" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.destinationOffice(CustomsOfficeList(Nil))
          result mustBe None
        }

        "customs office not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DestinationOfficePage)(customsOffice)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.destinationOffice(CustomsOfficeList(Nil))

          result mustBe None
        }
      }

      "return Some(Row)" - {

        "customs office found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DestinationOfficePage)(customsOffice)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.destinationOffice(CustomsOfficeList(Seq(customsOffice)))

          val label = msg"destinationOffice.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${customsOffice.name} (${customsOffice.id})"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.DestinationOfficeController.onPageLoad(lrn, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-destination-office")
                )
              )
            )
          )
        }
      }
    }

    "addAnotherTransitOffice" - {

      "return None" - {

        "AddAnotherTransitOfficePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.addAnotherTransitOffice(index, CustomsOfficeList(Nil))
          result mustBe None
        }

        "customs office not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddAnotherTransitOfficePage(index))(customsOffice.id)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.addAnotherTransitOffice(index, CustomsOfficeList(Nil))

          result mustBe None
        }
      }

      "return Some(Row)" - {

        "customs office found" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddAnotherTransitOfficePage(index))(customsOffice.id)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.addAnotherTransitOffice(index, CustomsOfficeList(Seq(customsOffice)))

          val label = msg"addAnotherTransitOffice.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"${customsOffice.name} (${customsOffice.id})"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.OfficeOfTransitCountryController.onPageLoad(lrn = lrn, index = index, mode = mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-office-of-transit-${index.display}")
                )
              )
            )
          )
        }
      }
    }

    "arrivalDatesAtOffice" - {

      "return None" - {

        "ArrivalDatesAtOfficePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.arrivalDatesAtOffice(index)
          result mustBe None
        }
      }

      "return Some(Row)" - {

        "ArrivalDatesAtOfficePage defined at index" in {

          val arrivalDate = arbitrary[LocalDateTime].sample.value
          val formattedArrivalDate =
            Format.dateFormattedDDMMYYYY(LocalDate.of(arrivalDate.getYear, arrivalDate.getMonth, arrivalDate.getDayOfMonth)).toLowerCase

          val answers = emptyUserAnswers.unsafeSetVal(ArrivalDatesAtOfficePage(index))(arrivalDate)

          val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.arrivalDatesAtOffice(index)

          val label = msg"arrivalDatesAtOffice.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$formattedArrivalDate"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ArrivalDatesAtOfficeController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-arrival-dates-at-office-of-transit-${index.display}")
                )
              )
            )
          )
        }
      }
    }
  }

}
