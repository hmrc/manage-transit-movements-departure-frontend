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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class RenderConfigImpl @Inject() (configuration: Configuration) extends RenderConfig {

  val contactHost: String                  = configuration.get[String]("contact-frontend.host")
  val contactFormServiceIdentifier: String = "CTCTraders"

  override val betaFeedbackUnauthenticatedUrl: String = s"$contactHost/contact/beta-feedback-unauthenticated"

  override val signOutUrl: String = configuration.get[String]("urls.logoutContinue") + configuration.get[String]("urls.feedback")

  override def timeoutSeconds: String = configuration.get[String]("session.timeoutSeconds")

  override def countdownSeconds: String = configuration.get[String]("session.countdownSeconds")
}

trait RenderConfig {
  def betaFeedbackUnauthenticatedUrl: String
  def signOutUrl: String
  def timeoutSeconds: String
  def countdownSeconds: String
  def contactFormServiceIdentifier: String
  def contactHost: String
}
