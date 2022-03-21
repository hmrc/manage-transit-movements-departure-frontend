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

package repositories

import config.FrontendAppConfig
import itSpecBase.ItSpecBase
import models.{EoriNumber, Id, LocalReferenceNumber, UserAnswers}
import org.mongodb.scala.MongoWriteException
import org.mongodb.scala.model.Filters
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext.Implicits.global

class SessionRepositorySpec
    extends ItSpecBase
    with BeforeAndAfterEach
    with GuiceOneAppPerSuite
    with OptionValues
    with IntegrationPatience
    with DefaultPlayMongoRepositorySupport[UserAnswers] {

  private val config: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  override protected def repository = new SessionRepository(mongoComponent, config)

  private lazy val userAnswers1 = UserAnswers(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("EoriNumber1"))
  private lazy val userAnswers2 = UserAnswers(LocalReferenceNumber("ABCD2222222222222").get, EoriNumber("EoriNumber2"))
  private lazy val userAnswers3 = UserAnswers(LocalReferenceNumber("ABCD3333333333333").get, EoriNumber("EoriNumber3"))

  override def beforeEach(): Unit = {
    super.beforeEach()
    insert(userAnswers1).futureValue
    insert(userAnswers2).futureValue
  }

  private def findOne(lrn: LocalReferenceNumber, eoriNumber: EoriNumber): Option[UserAnswers] =
    find(
      Filters.and(
        Filters.eq("lrn", lrn.value),
        Filters.eq("eoriNumber", eoriNumber.value)
      )
    ).futureValue.headOption

  "SessionRepository" - {

    "get" - {

      "must return UserAnswers when given an LocalReferenceNumber and EoriNumber" in {

        val result = repository.get(userAnswers1.lrn, userAnswers1.eoriNumber).futureValue

        result.value.lrn mustBe userAnswers1.lrn
        result.value.eoriNumber mustBe userAnswers1.eoriNumber
        result.value.data mustBe userAnswers1.data
      }

      "must return None when no UserAnswers match LocalReferenceNumber" in {

        val result = repository.get(userAnswers3.lrn, userAnswers1.eoriNumber).futureValue

        result mustBe None
      }

      "must return None when no UserAnswers match EoriNumber" in {

        val result = repository.get(userAnswers1.lrn, userAnswers3.eoriNumber).futureValue

        result mustBe None
      }
    }

    "set" - {

      "must create new document when given valid UserAnswers" in {

        findOne(userAnswers3.lrn, userAnswers3.eoriNumber) must not be defined

        val setResult = repository.set(userAnswers3).futureValue

        setResult mustBe true

        val getResult = findOne(userAnswers3.lrn, userAnswers3.eoriNumber).get

        getResult.lrn mustBe userAnswers3.lrn
        getResult.eoriNumber mustBe userAnswers3.eoriNumber
        getResult.data mustBe userAnswers3.data
      }

      "must update document when it already exists" in {

        val firstGet = findOne(userAnswers1.lrn, userAnswers1.eoriNumber).get

        val setResult = repository.set(userAnswers1.copy(data = Json.obj("foo" -> "bar"))).futureValue

        setResult mustBe true

        val secondGet = findOne(userAnswers1.lrn, userAnswers1.eoriNumber).get

        firstGet.id mustBe secondGet.id
        firstGet.lrn mustBe secondGet.lrn
        firstGet.eoriNumber mustBe secondGet.eoriNumber
        firstGet.data mustNot equal(secondGet.data)
        firstGet.lastUpdated isBefore secondGet.lastUpdated mustBe true
      }

      "must fail when attempting to set using an existing LocalReferenceNumber and EoriNumber with a different Id" in {

        val setResult = repository.set(userAnswers1.copy(id = Id()))

        whenReady(setResult.failed) {
          e =>
            e mustBe a[MongoWriteException]
        }
      }
    }

    "remove" - {

      "must remove document when given a valid LocalReferenceNumber and EoriNumber" in {

        findOne(userAnswers1.lrn, userAnswers1.eoriNumber) mustBe defined

        val removeResult = repository.remove(userAnswers1.lrn, userAnswers1.eoriNumber).futureValue

        removeResult mustBe true

        findOne(userAnswers1.lrn, userAnswers1.eoriNumber) must not be defined
      }

      "must not fail if document does not exist" in {

        findOne(userAnswers3.lrn, userAnswers3.eoriNumber) must not be defined

        val removeResult = repository.remove(userAnswers3.lrn, userAnswers3.eoriNumber).futureValue

        removeResult mustBe true
      }
    }
  }
}
