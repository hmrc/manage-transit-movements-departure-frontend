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

package utils.cyaHelpers.routeDetails

import base.SpecBase
import controllers.routeDetails.locationOfGoods.routes
import generators.Generators
import models.{LocationType, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.locationOfGoods.LocationOfGoodsTypePage
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class LocationOfGoodsCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "LocationOfGoodsCheckYourAnswersHelper" - {

    "locationOfGoodsType" - {
      "must return None" - {
        "when locationOfGoodsTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationOfGoodsType
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsTypePage defined" in {
          forAll(arbitrary[Mode], arbitrary[LocationType]) {
            (mode, locationType) =>
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsTypePage, locationType)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.locationOfGoodsType

              result mustBe Some(
                SummaryListRow(
                  key = Key("Location type".toText),
                  value = Value(messages(s"${LocationType.messageKeyPrefix}.$locationType").toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationOfGoodsTypeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"${LocationType.messageKeyPrefix}.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-type")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

  }
}
