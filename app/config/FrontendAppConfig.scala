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

package config

import com.google.inject.{Inject, Singleton}
import models.LocalReferenceNumber
import play.api.Configuration

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  val appName: String = configuration.get[String]("appName")

  lazy val nctsEnquiriesUrl: String = configuration.get[String]("urls.nctsEnquiries")
  lazy val nctsGuidanceUrl: String  = configuration.get[String]("urls.nctsGuidance")

  lazy val isPreLodgeEnabled: Boolean = configuration.get[Boolean]("features.isPreLodgeEnabled")

  lazy val customsReferenceDataUrl: String = configuration.get[Service]("microservice.services.customs-reference-data").fullServiceUrl

  lazy val loginUrl: String         = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")

  lazy val enrolmentKey: String           = configuration.get[String]("enrolment.key")
  lazy val enrolmentIdentifierKey: String = configuration.get[String]("enrolment.identifierKey")
  lazy val eccEnrolmentSplashPage: String = configuration.get[String]("urls.eccEnrolmentSplashPage")

  lazy val manageTransitMovementsUrl: String                = configuration.get[String]("urls.manageTransitMovementsFrontend")
  lazy val serviceUrl: String                               = s"$manageTransitMovementsUrl/what-do-you-want-to-do"
  lazy val manageTransitMovementsViewDeparturesUrl: String  = s"$manageTransitMovementsUrl/view-departure-declarations"
  lazy val manageTransitMovementsDraftDeparturesUrl: String = s"$manageTransitMovementsUrl/draft-declarations"

  lazy val enrolmentProxyUrl: String = configuration.get[Service]("microservice.services.enrolment-store-proxy").fullServiceUrl

  lazy val cacheUrl: String = configuration.get[Service]("microservice.services.manage-transit-movements-departure-cache").fullServiceUrl

  def traderDetailsFrontendUrl(lrn: LocalReferenceNumber): String    = frontendUrl(lrn, "traderDetails")
  def routeDetailsFrontendUrl(lrn: LocalReferenceNumber): String     = frontendUrl(lrn, "routeDetails")
  def transportDetailsFrontendUrl(lrn: LocalReferenceNumber): String = frontendUrl(lrn, "transportDetails")
  def guaranteeDetailsFrontendUrl(lrn: LocalReferenceNumber): String = frontendUrl(lrn, "guaranteeDetails")
  def documentsFrontendUrl(lrn: LocalReferenceNumber): String        = frontendUrl(lrn, "documents")
  def itemsFrontendUrl(lrn: LocalReferenceNumber): String            = frontendUrl(lrn, "items")

  private def frontendUrl(lrn: LocalReferenceNumber, section: String): String = {
    val url: String = configuration.get[String](s"urls.${section}Frontend")
    url.replace(":lrn", lrn.toString)
  }
}
