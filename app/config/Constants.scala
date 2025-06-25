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

object Constants {

  object CountryCode {
    val GB = "GB"
    val XI = "XI"
    val AD = "AD"
  }

  object DeclarationType {
    val TIR  = "TIR"
    val T2SM = "T2SM"
  }

  object SecurityType {
    val NoSecurity = "0"
    val EXS        = "2"
  }
}
