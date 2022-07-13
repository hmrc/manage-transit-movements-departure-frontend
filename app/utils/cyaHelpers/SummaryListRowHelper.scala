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

package utils.cyaHelpers

import models.reference.CountryCode
import models.{Address, CountryList}
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._

import scala.math.BigDecimal.RoundingMode

private[utils] class SummaryListRowHelper(implicit messages: Messages) {

  protected def formatAsYesOrNo(answer: Boolean): Content =
    if (answer) {
      messages("site.yes").toText
    } else {
      messages("site.no").toText
    }

  protected def formatAsAddress(address: Address): Content =
    HtmlContent(Seq(address.line1, address.line2, address.postalCode, address.country.description).mkString("<br>"))

  protected def formatAsText[T](answer: T): Content = s"$answer".toText

  protected def formatAsPassword(answer: String): Content = ("•" * answer.length).toText

  /**
    * @param answer the value to be formatted
    * @return the value, comma separated if necessary, in pounds and pence
    */
  protected def formatAsCurrency(answer: BigDecimal): Content = {
    val str            = String.valueOf(answer.setScale(2, RoundingMode.HALF_UP))
    val numberOfDigits = str.takeWhile(_ != '.').length
    str.zipWithIndex
      .foldLeft("£") {
        case (acc, (char, index)) =>
          if (index % 3 == numberOfDigits % 3 && index > 0 && index < numberOfDigits) {
            acc + ',' + char
          } else {
            acc + char
          }
      }
      .toText
  }

  protected def formatEnumAsText[T](messageKeyPrefix: String)(answer: T): Content =
    formatEnumAsString(messageKeyPrefix)(answer).toText

  protected def formatEnumAsString[T](messageKeyPrefix: String)(answer: T): String =
    messages(s"$messageKeyPrefix.$answer")

  protected def formatAsCountry(countryList: CountryList)(answer: CountryCode): Content =
    s"${countryList.getCountry(answer).map(_.description).getOrElse(answer.code)}".toText

  protected def buildRow(
    prefix: String,
    answer: Content,
    id: Option[String],
    call: Call,
    args: Any*
  ): SummaryListRow =
    buildSimpleRow(
      prefix = prefix,
      label = messages(s"$prefix.checkYourAnswersLabel", args: _*).toText,
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
      label = messages(s"$prefix.checkYourAnswersLabel", args: _*).toText,
      answer = answer,
      id = None,
      call = None
    )

  private def buildSimpleRow(
    prefix: String,
    label: Content,
    answer: Content,
    id: Option[String],
    call: Option[Call],
    args: Any*
  ): SummaryListRow =
    SummaryListRow(
      key = Key(label),
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
