package views.behaviours

import models.DynamicAddress
import org.scalacheck.Arbitrary

trait DynamicAddressViewBehaviours extends QuestionViewBehaviours[DynamicAddress] {

  val fields: Seq[String]           = Seq("numberAndStreet", "city", "postalCode")
  val isPostalCodeRequired: Boolean = Arbitrary.arbitrary[Boolean].sample.value

  def pageWithAddressInput(): Unit =
    "page with an address input" - {

      "when rendered" - {

        for (field <- fields) {
          s"must contain an input for \$field" in {
            assertRenderedById(doc, field)
          }

          s"must contain a label for the field '\$field'" in {
            val labels = doc.getElementsByAttributeValue("for", field)
            labels.size mustEqual 1

            (field, isPostalCodeRequired) match {
              case ("postalCode", false) =>
                assertElementContainsText(labels.first(), messages(s"\$prefix.\$field.optional"))
              case _ => assertElementContainsText(labels.first(), messages(s"\$prefix.\$field"))
            }
          }
        }

        behave like pageWithoutErrorSummary()
      }

      for (field <- fields)
        s"when rendered with an error for field '\$field'" - {

          behave like pageWithErrorSummary(field)

          s"must show an error in the label for field '\$field'" in {
            val formGroupError = getElementByClass(docWithError(field), "govuk-form-group--error")
            formGroupError.getElementsByClass("govuk-label").first().attr("for") mustEqual field
            formGroupError.getElementsByClass("govuk-error-message").first().id() mustEqual s"\$field-error"
          }
        }
    }
}
