package views.behaviours

import java.time.LocalDate

import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.FormError

trait DateInputViewBehaviours extends QuestionViewBehaviours[LocalDate] with ScalaCheckPropertyChecks {

  // scalastyle:off method.length
  def pageWithDateInput(): Unit =
    "page with date input" - {
      "when rendered" - {

        "must display day" in {
          assertRenderedById(doc, "value_day")
        }

        "must display month" in {
          assertRenderedById(doc, "value_month")
        }

        "must display year" in {
          assertRenderedById(doc, "value_year")
        }
      }

      "when rendered with error" - {
        behave like pageWithErrorSummary()

        "must show an error class on the inputs" in {
          val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "month", "year")))))

          val dayInput = docWithError.getElementById("value_day")
          assert(dayInput.hasClass("govuk-input--error"))

          val monthInput = docWithError.getElementById("value_month")
          assert(monthInput.hasClass("govuk-input--error"))

          val yearInput = docWithError.getElementById("value_year")
          assert(yearInput.hasClass("govuk-input--error"))
        }

        "must have correct href on error link" - {
          "when no args" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value_day")
          }

          "when error in day input" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value_day")
          }

          "when error in month input" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("month")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value_month")
          }

          "when error in year input" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value_year")
          }

          "when error in day and month inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "month")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value_day")
          }

          "when error in day and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value_day")
          }

          "when error in month and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("month", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value_month")
          }

          "when error in day, month and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "month", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value_day")
          }

          "when error has other args" in {
            forAll(arbitrary[String]) {
              arg =>
                val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq(arg)))))
                val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
                assertElementContainsHref(link, "#value_day")
            }
          }
        }
      }
    }
  // scalastyle:on method.length
}
