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

package pages.transport.transportMeans.departure

import models.transport.transportMeans.departure.InlandMode
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours
import pages.sections.transport.{TransportMeansActiveListSection, TransportMeansDepartureSection}
import play.api.libs.json.{JsArray, Json}

class InlandModePageSpec extends PageBehaviours {

  "InlandModePage" - {

    beRetrievable[InlandMode](InlandModePage)

    beSettable[InlandMode](InlandModePage)

    beRemovable[InlandMode](InlandModePage)

    "cleanup" - {
      "when answer changes to something that isn't mail" - {
        "must remove departure section" in {
          forAll(arbitrary[InlandMode]) {
            inlandMode =>
              val userAnswers = emptyUserAnswers
                .setValue(InlandModePage, inlandMode)
                .setValue(TransportMeansDepartureSection, Json.obj("foo" -> "bar"))
                .setValue(TransportMeansActiveListSection, JsArray(Seq(Json.obj("foo" -> "bar"))))

              forAll(Gen.oneOf(InlandMode.values).filterNot(_ == InlandMode.Mail).filterNot(_ == inlandMode)) {
                differentInlandModeNotMail =>
                  val result = userAnswers.setValue(InlandModePage, differentInlandModeNotMail)

                  result.get(TransportMeansDepartureSection) must not be defined
                  result.get(TransportMeansActiveListSection) mustBe defined
              }
          }
        }
      }
    }

    "when answer changes to Mail" - {
      "must remove departure and active sections" in {
        forAll(arbitrary[InlandMode].suchThat(_ != InlandMode.Mail)) {
          inlandMode =>
            val userAnswers = emptyUserAnswers
              .setValue(InlandModePage, inlandMode)
              .setValue(TransportMeansDepartureSection, Json.obj("foo" -> "bar"))
              .setValue(TransportMeansActiveListSection, JsArray(Seq(Json.obj("foo" -> "bar"))))

            val result = userAnswers.setValue(InlandModePage, InlandMode.Mail)

            result.get(TransportMeansDepartureSection) must not be defined
            result.get(TransportMeansActiveListSection) must not be defined
        }
      }
    }
  }
}
