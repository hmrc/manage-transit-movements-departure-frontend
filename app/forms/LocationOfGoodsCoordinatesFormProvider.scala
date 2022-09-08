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

package forms

import forms.mappings.Mappings
import models.Coordinates
import models.domain.StringFieldRegex._
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

import javax.inject.Inject

class LocationOfGoodsCoordinatesFormProvider @Inject() extends Mappings {

  def apply(prefix: String)(implicit messages: Messages): Form[Coordinates] =
    Form(
      mapping(
        "latitude" -> {
          lazy val args = Seq("latitude")
          trimmedText(s"$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                regexp(coordinatesCharacterRegex, s"$prefix.error.invalid", args),
                regexp(coordinateFormatRegex, s"$prefix.error.format", args),
                regexp(coordinatesLatitudeMaxRegex.r, s"$prefix.error.latitude.maximum", args)
              )
            )
        },
        "longitude" -> {
          lazy val args = Seq("longitude")
          trimmedText(s"$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                regexp(coordinatesCharacterRegex, s"$prefix.error.invalid", args),
                regexp(coordinateFormatRegex, s"$prefix.error.format", args),
                regexp(coordinatesLongitudeMaxRegex.r, s"$prefix.error.longitude.maximum", args)
              )
            )
        }
      )(Coordinates.apply)(Coordinates.unapply)
    )
}
