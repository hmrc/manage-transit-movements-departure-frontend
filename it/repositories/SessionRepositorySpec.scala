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
import models.{EoriNumber, LocalReferenceNumber, UserAnswers}
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

  private val userAnswer1 = UserAnswers(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("EoriNumber1"), Json.obj("foo" -> "bar"))
  private val userAnswer2 = UserAnswers(LocalReferenceNumber("ABCD2222222222222").get, EoriNumber("EoriNumber2"), Json.obj("bar" -> "foo"))

  override def beforeEach(): Unit = {
    super.beforeEach()
    insert(userAnswer1).futureValue
    insert(userAnswer2).futureValue
  }

  "SessionRepository" - {

    "get" - {

      "must return UserAnswers when given an LocalReferenceNumber and EoriNumber" in {

        val result = repository.get(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("EoriNumber1")).futureValue

        result.value.lrn mustBe userAnswer1.lrn
        result.value.eoriNumber mustBe userAnswer1.eoriNumber
        result.value.data mustBe userAnswer1.data
      }

      "must return None when no UserAnswers match LocalReferenceNumber" in {

        val result = repository.get(LocalReferenceNumber("ABCD3333333333333").get, EoriNumber("EoriNumber1")).futureValue

        result mustBe None
      }

      "must return None when no UserAnswers match EoriNumber" in {

        val result = repository.get(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("InvalidEori")).futureValue

        result mustBe None
      }
    }

    "set" - {

      "must create new document when given valid UserAnswers" in {

        val userAnswer = UserAnswers(LocalReferenceNumber("ABCD3333333333333").get, EoriNumber("EoriNumber3"), Json.obj("foo" -> "bar"))

        val setResult = repository.set(userAnswer).futureValue

        setResult mustBe true

        val getResult = find(
          Filters.and(
            Filters.eq("lrn", "ABCD3333333333333"),
            Filters.eq("eoriNumber", "EoriNumber3")
          )
        ).futureValue.head

        getResult.lrn mustBe userAnswer.lrn
        getResult.eoriNumber mustBe userAnswer.eoriNumber
        getResult.data mustBe userAnswer.data
      }

      "must fail when attempting to create using an existing id" in {

        val userAnswer = UserAnswers(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("EoriNumber1"), Json.obj("foo" -> "bar"))

        val setResult = repository.set(userAnswer)

        whenReady(setResult.failed) {
          e =>
            e mustBe a[MongoWriteException]
        }
      }
    }

    "remove" - {

      "must remove document when given a valid LocalReferenceNumber and EoriNumber" in {

        find(
          Filters.and(
            Filters.eq("lrn", "ABCD1111111111111"),
            Filters.eq("eoriNumber", "EoriNumber1")
          )
        ).futureValue.headOption mustBe defined

        repository.remove(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("EoriNumber1")).futureValue

        find(
          Filters.and(
            Filters.eq("lrn", "ABCD1111111111111"),
            Filters.eq("eoriNumber", "EoriNumber1")
          )
        ).futureValue.headOption must not be defined
      }
    }
  }
}
