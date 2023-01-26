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

package api.submission

import generated._
import models.journeyDomain.traderDetails.TraderDetailsDomain

object Representative {

  def transform(domain: TraderDetailsDomain): Option[RepresentativeType05] =
    domain.representative.map {
      r =>
        RepresentativeType05(
          r.eori.value,
          r.capacity.toString,
          Some(ContactPersonType05(r.name, r.phone, None))
        )
    }
}
