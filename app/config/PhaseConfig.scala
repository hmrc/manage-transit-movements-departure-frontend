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

// TODO - these two headers fundamentally do the same thing.
//  Once the relevant frontend and backend changes have been deployed we can remove `acceptHeader`.
trait PhaseConfig {
  val acceptHeader: String
  val apiVersionHeader: String
}

class TransitionConfig() extends PhaseConfig {
  override val acceptHeader: String     = "application/vnd.hmrc.transition+json"
  override val apiVersionHeader: String = "transitional"
}

class PostTransitionConfig() extends PhaseConfig {
  override val acceptHeader: String     = "application/vnd.hmrc.final+json"
  override val apiVersionHeader: String = "final"
}
