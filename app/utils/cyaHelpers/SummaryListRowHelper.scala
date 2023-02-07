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

package utils.cyaHelpers

import models.reference.{Country, CurrencyCode}
import models.{DateTime, DynamicAddress, PostalCodeAddress}
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import utils.Format.RichDateTime

import java.text.NumberFormat
import java.util.Currency

private[utils] class SummaryListRowHelper(implicit messages: Messages) {

  protected def formatAsYesOrNo(answer: Boolean): Content =
    if (answer) {
      messages("site.yes").toText
    } else {
      messages("site.no").toText
    }

  def formatAsDateTime(answer: DateTime): Content =
    answer.formatAsString.toText

  protected def formatAsDynamicAddress(address: DynamicAddress): Content =
    HtmlContent(address.toString)

  protected def formatAsPostalCodeAddress(address: PostalCodeAddress): Content =
    HtmlContent(address.toString)

  protected def formatAsText[T](answer: T): Content = s"$answer".toText

  protected def formatAsPassword(answer: String): Content = ("•" * answer.length).toText

  protected def formatAsCurrency(answer: BigDecimal, currencyCode: CurrencyCode): Content = {
    val str =
      try {
        val format   = NumberFormat.getCurrencyInstance()
        val currency = Currency.getInstance(currencyCode.currency)
        format.setCurrency(currency)
        format.format(answer)
      } catch {
        case _: Throwable => s"$answer ${currencyCode.currency}"
      }

    str.toText
  }

  protected def formatEnumAsText[T](messageKeyPrefix: String)(answer: T): Content =
    formatEnumAsString(messageKeyPrefix)(answer).toText

  protected def formatEnumAsString[T](messageKeyPrefix: String)(answer: T): String =
    messages(s"$messageKeyPrefix.$answer")

  protected def formatAsCountry(country: Country): Content = country.description.toText

  protected def buildRow(
    prefix: String,
    answer: Content,
    id: Option[String],
    call: Call,
    args: Any*
  ): SummaryListRow =
    buildSimpleRow(
      prefix = prefix,
      label = messages(s"$prefix.checkYourAnswersLabel", args: _*),
      answer = answer,
      id = id,
      call = Some(call),
      args = args: _*
    )

  protected def buildRowWithNoChangeLink(
    prefix: String,
    answer: Content,
    args: Any*
  ): SummaryListRow =
    buildSimpleRow(
      prefix = prefix,
      label = messages(s"$prefix.checkYourAnswersLabel", args: _*),
      answer = answer,
      id = None,
      call = None
    )

  protected def buildSimpleRow(
    prefix: String,
    label: String,
    answer: Content,
    id: Option[String],
    call: Option[Call],
    args: Any*
  ): SummaryListRow =
    SummaryListRow(
      key = label.toKey,
      value = Value(answer),
      actions = call.map {
        route =>
          Actions(
            items = List(
              ActionItem(
                content = messages("site.edit").toText,
                href = route.url,
                visuallyHiddenText = Some(messages(s"$prefix.change.hidden", args: _*)),
                attributes = id.fold[Map[String, String]](Map.empty)(
                  id => Map("id" -> id)
                )
              )
            )
          )
      }
    )

}
