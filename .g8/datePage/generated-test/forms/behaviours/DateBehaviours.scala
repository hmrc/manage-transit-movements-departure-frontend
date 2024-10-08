package forms.behaviours

import org.scalacheck.Gen
import play.api.data.{Form, FormError}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateBehaviours extends FieldBehaviours {

  def dateField(form: Form[?], key: String, validData: Gen[LocalDate]): Unit =
    "must bind valid data" in {

      forAll(validData -> "valid date") {
        date =>
          val data = Map(
            s"\$key.day"   -> date.getDayOfMonth.toString,
            s"\$key.month" -> date.getMonthValue.toString,
            s"\$key.year"  -> date.getYear.toString
          )

          val result = form.bind(data)

          result.value.value mustEqual date
      }
    }

  def dateFieldWithMax(form: Form[?], key: String, max: LocalDate, formError: FormError): Unit =
    s"must fail to bind a date greater than \${max.format(DateTimeFormatter.ISO_LOCAL_DATE)}" in {

      val generator = datesBetween(max.plusDays(1), max.plusYears(10))

      forAll(generator -> "invalid dates") {
        date =>
          val data = Map(
            s"\$key.day"   -> date.getDayOfMonth.toString,
            s"\$key.month" -> date.getMonthValue.toString,
            s"\$key.year"  -> date.getYear.toString
          )

          val result = form.bind(data)

          result.errors must contain only formError
      }
    }

  def dateFieldWithMin(form: Form[?], key: String, min: LocalDate, formError: FormError): Unit =
    s"must fail to bind a date earlier than \${min.format(DateTimeFormatter.ISO_LOCAL_DATE)}" in {

      val generator = datesBetween(min.minusYears(10), min.minusDays(1))

      forAll(generator -> "invalid dates") {
        date =>
          val data = Map(
            s"\$key.day"   -> date.getDayOfMonth.toString,
            s"\$key.month" -> date.getMonthValue.toString,
            s"\$key.year"  -> date.getYear.toString
          )

          val result = form.bind(data)

          result.errors must contain only formError
      }
    }

  def mandatoryDateField(form: Form[?], key: String, requiredAllKey: String, errorArgs: Seq[String] = Seq.empty): Unit =
    "must fail to bind an empty date" in {

      val result = form.bind(Map.empty[String, String])

      result.errors must contain only FormError(key, requiredAllKey, errorArgs)
    }
}
