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

package views.behaviours

import generators.Generators
import org.jsoup.nodes.Document
import org.scalacheck.Arbitrary.arbitrary
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

import scala.collection.JavaConverters._

trait ListWithActionsViewBehaviours extends YesNoViewBehaviours with Generators {

  def maxNumber: Int

  private val listItem = arbitrary[ListItem].sample.value

  val listItems: Seq[ListItem] = Seq(listItem)

  val maxedOutListItems: Seq[ListItem] = Seq.fill(maxNumber)(listItem)

  def applyMaxedOutView: HtmlFormat.Appendable

  def pageWithMoreItemsAllowed(additionalBehaviours: Unit = ()): Unit =
    "page with more items allowed" - {

      behave like pageWithTitle(doc, s"$prefix.singular", listItems.length)

      behave like pageWithHeading(doc, s"$prefix.singular", listItems.length)

      behave like pageWithListWithActions(doc, listItems)

      behave like pageWithRadioItems(legendIsHeading = false)

      additionalBehaviours
    }

  def pageWithItemsMaxedOut(): Unit =
    "page with items maxed out" - {

      val doc = parseView(applyMaxedOutView)

      behave like pageWithTitle(doc, s"$prefix.plural", maxedOutListItems.length)

      behave like pageWithHeading(doc, s"$prefix.plural", maxedOutListItems.length)

      behave like pageWithListWithActions(doc, maxedOutListItems)

      behave like pageWithoutRadioItems(doc)

      behave like pageWithContent(doc, "p", messages(s"$prefix.maxLimit.label"))
    }

  private def pageWithListWithActions(doc: Document, listItems: Seq[ListItem]): Unit =
    "page with a list with actions" - {
      "must contain a description list" in {
        val descriptionLists = getElementsByTag(doc, "dl")
        descriptionLists.size mustBe 1
      }

      val renderedItems = doc.getElementsByClass("govuk-summary-list__row").asScala

      listItems.zipWithIndex.foreach {
        case (listItem, index) =>
          val renderedItem = renderedItems(index)

          s"item ${index + 1}" - {
            "must contain a name" in {
              val name = renderedItem.getElementsByClass("govuk-summary-list__key").text()
              name mustBe listItem.name
            }

            "must contain 2 actions" in {
              val actions = renderedItem.getElementsByClass("govuk-summary-list__actions-list-item")
              actions.size() mustBe 2
            }

            def withActionLink(linkType: String, index: Int, url: String): Unit =
              s"must contain a $linkType link" in {
                val link = renderedItem
                  .getElementsByClass("govuk-summary-list__actions-list-item")
                  .asScala(index)
                  .getElementsByClass("govuk-link")
                  .first()

                assertElementContainsHref(link, url)

                val spans = link.getElementsByTag("span")
                spans.size() mustBe 2

                spans.first().text() mustBe linkType
                assert(spans.first().hasAttr("aria-hidden"))

                spans.last().text() mustBe s"$linkType ${listItem.name}"
                assert(spans.last().hasClass("govuk-visually-hidden"))
              }

            withActionLink("Change", 0, listItem.changeUrl)
            withActionLink("Remove", 1, listItem.removeUrl)
          }
      }
    }
}
