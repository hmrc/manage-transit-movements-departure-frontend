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

import models.domain.UserAnswersReader
import models.journeyDomain.JourneyDomainModel
import models.{LocalReferenceNumber, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.Section
import play.api.i18n.Messages
import play.api.libs.json.{JsArray, Reads}
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.html.components.{Content, SummaryListRow}
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

class AnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends SummaryListRowHelper {

  protected def lrn: LocalReferenceNumber = userAnswers.lrn

  protected def getAnswerAndBuildRow[T](
    page: QuestionPage[T],
    formatAnswer: T => Content,
    prefix: String,
    id: Option[String],
    args: Any*
  )(implicit rds: Reads[T]): Option[SummaryListRow] =
    for {
      answer <- userAnswers.get(page)
      call   <- page.route(userAnswers, mode)
    } yield buildRow(
      prefix = prefix,
      answer = formatAnswer(answer),
      id = id,
      call = call,
      args = args: _*
    )

  protected def getAnswerAndBuildRowWithDynamicLink[T](
    page: QuestionPage[T],
    formatAnswer: T => Content,
    prefix: String,
    id: Option[String],
    args: Any*
  )(predicate: T => Boolean)(implicit rds: Reads[T]): Option[SummaryListRow] =
    for {
      answer <- userAnswers.get(page)
      call   <- page.route(userAnswers, mode)
    } yield
      if (predicate(answer)) {
        buildRowWithNoChangeLink(
          prefix = prefix,
          answer = formatAnswer(answer),
          args = args: _*
        )
      } else {
        buildRow(
          prefix = prefix,
          answer = formatAnswer(answer),
          id = id,
          call = call,
          args = args: _*
        )
      }

  protected def buildListItems(
    section: Section[JsArray]
  )(block: Int => Option[Either[ListItem, ListItem]]): Seq[Either[ListItem, ListItem]] =
    userAnswers
      .get(section)
      .map {
        _.value.zipWithIndex.flatMap {
          case (_, index) =>
            block(index)
        }
      }
      .getOrElse(Nil)

  protected def buildListItem[A <: JourneyDomainModel, B](
    page: QuestionPage[B],
    getName: A => B,
    formatName: B => String,
    removeRoute: Call
  )(implicit userAnswersReader: UserAnswersReader[A], rds: Reads[B]): Option[Either[ListItem, ListItem]] =
    UserAnswersReader[A].run(userAnswers) match {
      case Left(readerError) =>
        readerError.page.route(userAnswers, mode).flatMap {
          changeRoute =>
            getNameAndBuildListItem[B](
              page = page,
              formatName = formatName,
              changeUrl = changeRoute.url,
              removeUrl = removeRoute.url
            ).map(Left(_))
        }
      case Right(journeyDomainModel) =>
        journeyDomainModel.routeIfCompleted(userAnswers).map {
          changeRoute =>
            Right(
              ListItem(
                name = formatName(getName(journeyDomainModel)),
                changeUrl = changeRoute.url,
                removeUrl = removeRoute.url
              )
            )
        }
    }

  protected def getNameAndBuildListItem[T](
    page: QuestionPage[T],
    formatName: T => String,
    changeUrl: String,
    removeUrl: String
  )(implicit rds: Reads[T]): Option[ListItem] =
    userAnswers.get(page) map {
      name =>
        ListItem(
          name = formatName(name),
          changeUrl = changeUrl,
          removeUrl = removeUrl
        )
    }
}
