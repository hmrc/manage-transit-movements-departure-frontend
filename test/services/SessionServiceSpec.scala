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

package services

import base.SpecBase
import generators.Generators
import models.LocalReferenceNumber
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest

class SessionServiceSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val sessionService = new SessionService()

  "get" - {
    "when LRN exists" - {
      "must return Some value" in {
        forAll(arbitrary[String]) {
          lrn =>
            implicit val request: FakeRequest[?] = fakeRequest.withSession(SessionService.key -> lrn)
            sessionService.get.get mustEqual lrn
        }
      }
    }

    "when LRN does not exist" - {
      "must return None" in {
        implicit val request: FakeRequest[?] = fakeRequest
        sessionService.get mustNot be(defined)
      }
    }
  }

  "set" - {
    "must set LRN in session" in {
      forAll(arbitrary[LocalReferenceNumber]) {
        lrn =>
          implicit val request: FakeRequest[?] = fakeRequest
          val resultBefore                     = Ok
          resultBefore.session.get(SessionService.key) mustNot be(defined)
          val resultAfter = sessionService.set(resultBefore, lrn)
          resultAfter.session.get(SessionService.key).get mustEqual lrn.toString
      }
    }

    "must overwrite LRN in session" in {
      forAll(arbitrary[LocalReferenceNumber], arbitrary[LocalReferenceNumber]) {
        (lrn1, lrn2) =>
          implicit val request: FakeRequest[?] = fakeRequest.withSession(SessionService.key -> lrn1.toString)
          val resultBefore                     = Ok
          resultBefore.session.get(SessionService.key) must be(defined)
          val resultAfter = sessionService.set(resultBefore, lrn2)
          resultAfter.session.get(SessionService.key).get mustEqual lrn2.toString
      }
    }
  }

  "remove" - {
    "must remove LRN from session" - {
      "when there isn't an LRN in the session" in {
        implicit val request: FakeRequest[?] = fakeRequest
        val resultBefore                     = Ok
        resultBefore.session.get(SessionService.key) mustNot be(defined)
        val resultAfter = sessionService.remove(resultBefore)
        resultAfter.session.get(SessionService.key) mustNot be(defined)
      }

      "when there is an LRN in the session" in {
        forAll(arbitrary[LocalReferenceNumber]) {
          lrn =>
            implicit val request: FakeRequest[?] = fakeRequest.withSession(SessionService.key -> lrn.toString)
            val resultBefore                     = Ok
            resultBefore.session.get(SessionService.key) must be(defined)
            val resultAfter = sessionService.remove(resultBefore)
            resultAfter.session.get(SessionService.key) mustNot be(defined)
        }
      }
    }
  }
}
