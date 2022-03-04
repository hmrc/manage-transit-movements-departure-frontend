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

package models.domain

object StringFieldRegex {

  val stringFieldRegex                = "[\\sa-zA-Z0-9&'@/.\\-? ]*".r
  val alphaNumericRegex               = "^[a-zA-Z0-9]*$".r
  val alphaNumericWithSpaceRegex      = "^[a-zA-Z0-9 ]*$".r
  val commodityCodeCharactersRegex    = "^[0-9]*$"
  val commodityCodeFormatRegex        = "^([0-9]{6}|[0-9]{8})$"
  val liabilityAmountCharactersRegex  = "^$|^[0-9.]*$"
  val liabilityAmountFormatRegex      = "^$|([0-9]*(?:\\.[0-9]{1,2})?)$"
  val greaterThanZeroRegex            = "^$|([1-9]{1}[0-9.]*)$"
  val zeroOrGreaterThanOneRegex       = "^(0)$|^(0\\.0)$|^(0\\.00)$|^([1-9]{1}[0-9.]*)$"
  val eoriNumberRegex: String         = "^[a-zA-Z]{2}[0-9a-zA-Z]{1,15}"
  val consignorNameRegex              = "^[a-zA-Z0-9&'@\\/.\\-%? ]{1,35}"
  val authorisedLocationCodeRegex     = "^[a-zA-Z0-9&'@/.%-? ]*$"
  val principalTirHolderIdFormatRegex = "^[a-zA-Z]{3}\\/[0-9]{3}\\/[0-9]{7}$"
  val principalTirHolderIdCharRegex   = "[a-zA-Z0-9\\/]*".r

}
