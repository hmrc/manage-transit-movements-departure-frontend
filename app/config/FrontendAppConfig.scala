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
import play.api.Configuration

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  val appName: String = configuration.get[String]("appName")

  lazy val daysBefore: Int = configuration.get[Int]("limits.officeOfTransitETA.daysBefore")
  lazy val daysAfter: Int  = configuration.get[Int]("limits.officeOfTransitETA.daysAfter")

  lazy val nctsEnquiriesUrl: String = configuration.get[String]("urls.nctsEnquiries")
  lazy val nctsGuidanceUrl: String  = configuration.get[String]("urls.nctsGuidance")

  //TODO: Move out into it's own config object like `ManageTransitMovementsService`
  lazy val referenceDataUrl: String = configuration.get[Service]("microservice.services.referenceData").fullServiceUrl

  //TODO: Move out into it's own config object like `ManageTransitMovementsService`
  lazy val departureHost: String    = configuration.get[Service]("microservice.services.departures").fullServiceUrl
  lazy val departureBaseUrl: String = configuration.get[Service]("microservice.services.departures").baseUrl

  // TODO: Move config values for IdentifierAction to it's own config class
  // TODO: Make these values eagerly evaluated. I.e. non lazy
  lazy val authUrl: String          = configuration.get[Service]("auth").baseUrl
  lazy val loginUrl: String         = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")

  lazy val legacyEnrolmentKey: String           = configuration.get[String]("keys.legacy.enrolmentKey")
  lazy val legacyEnrolmentIdentifierKey: String = configuration.get[String]("keys.legacy.enrolmentIdentifierKey")
  lazy val newEnrolmentKey: String              = configuration.get[String]("keys.enrolmentKey")
  lazy val newEnrolmentIdentifierKey: String    = configuration.get[String]("keys.enrolmentIdentifierKey")
  lazy val eccEnrolmentSplashPage: String       = configuration.get[String]("urls.eccEnrolmentSplashPage")

  lazy val manageTransitMovementsUrl: String               = configuration.get[String]("urls.manageTransitMovementsFrontend")
  lazy val serviceUrl: String                              = s"$manageTransitMovementsUrl/what-do-you-want-to-do"
  lazy val manageTransitMovementsViewDeparturesUrl: String = s"$manageTransitMovementsUrl/view-departures"

  lazy val enrolmentProxyUrl: String = configuration.get[Service]("microservice.services.enrolment-store-proxy").fullServiceUrl

  lazy val cacheUrl: String = configuration.get[Service]("microservice.services.manage-transit-movements-departure-cache").fullServiceUrl

  lazy val maxGuarantees: Int             = configuration.get[Int]("limits.maxGuarantees")
  lazy val maxCountriesOfRouting: Int     = configuration.get[Int]("limits.maxCountriesOfRouting")
  lazy val maxOfficesOfTransit: Int       = configuration.get[Int]("limits.maxOfficesOfTransit")
  lazy val maxOfficesOfExit: Int          = configuration.get[Int]("limits.maxOfficesOfExit")
  lazy val maxActiveBorderTransports: Int = configuration.get[Int]("limits.maxActiveBorderTransports")

  lazy val apiUrl = configuration.get[Service]("microservice.services.common-transit-convention-traders").baseUrl

  lazy val declarationEnabled: Boolean = configuration.get[Boolean]("declaration.enabled")
}
