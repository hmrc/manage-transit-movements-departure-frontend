/*
 * Copyright 2024 HM Revenue & Customs
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

package config

import com.google.inject.{Inject, Singleton}
import controllers.routes
import models.LocalReferenceNumber
import play.api.Configuration
import play.api.i18n.Messages
import play.api.mvc.Request

@Singleton
class RenderConfigImpl @Inject() (configuration: Configuration) extends RenderConfig {

  override val signOutUrl: String = configuration.get[String]("urls.logoutContinue") + configuration.get[String]("urls.feedback")

  override val timeoutSeconds: Int = configuration.get[Int]("session.timeoutSeconds")

  override val countdownSeconds: Int = configuration.get[Int]("session.countdownSeconds")

  override val showUserResearchBanner: Boolean = configuration.get[Boolean]("banners.showUserResearch")
  override val userResearchUrl: String         = configuration.get[String]("urls.userResearch")

  override def signOutAndUnlockUrl(lrn: Option[LocalReferenceNumber]): String = lrn.map(routes.DeleteLockController.delete(_, None).url).getOrElse(signOutUrl)

  override val isTraderTest: Boolean = configuration.get[Boolean]("trader-test.enabled")
  override val feedbackEmail: String = configuration.get[String]("trader-test.feedback.email")
  override val feedbackForm: String  = configuration.get[String]("trader-test.feedback.link")

  override def mailto(implicit request: Request[?], messages: Messages): String = {
    val subject = messages("site.email.subject")
    val body = {
      val newLine      = "%0D%0A"
      val newParagraph = s"$newLine$newLine"
      s"""
         |URL: ${request.uri}$newParagraph
         |Tell us how we can help you here.$newParagraph
         |Give us a brief description of the issue or question, including details like…$newLine
         | - The screens where you experienced the issue$newLine
         | - What you were trying to do at the time$newLine
         | - The information you entered$newParagraph
         |Please include your name and phone number and we’ll get in touch.
         |""".stripMargin
    }

    s"mailto:$feedbackEmail?subject=$subject&body=$body"
  }
}

trait RenderConfig {
  def signOutAndUnlockUrl(lrn: Option[LocalReferenceNumber]): String
  val signOutUrl: String
  val timeoutSeconds: Int
  val countdownSeconds: Int
  val showUserResearchBanner: Boolean
  val userResearchUrl: String
  val isTraderTest: Boolean
  val feedbackEmail: String
  val feedbackForm: String
  def mailto(implicit request: Request[?], messages: Messages): String
}
