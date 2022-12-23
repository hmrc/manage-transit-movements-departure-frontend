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

package views

import play.api.data.FormError
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.errorsummary.ErrorLink

object Foo {

  implicit class RichFormErrors(formErrors: Seq[FormError])(implicit messages: Messages) {

    def fooErrorLink: Seq[ErrorLink] =
      fooErrorLinks(Text.apply)

    def fooErrorLinks(contentConstructor: String => Content): Seq[ErrorLink] =
      formErrors.map {
        formError =>
          val getArg = formError.args.headOption.getOrElse("").toString

          val key = getArg
            .replaceAll("\\s", "")
            .toIntOption match {
            case Some(_) => s"#${formError.key}"
            case _       => s"#${formError.key}${getArg.capitalize}"
          }

          ErrorLink(href = Some(key), content = contentConstructor(errorMessage(formError)))
      }

    def errorMessage(formError: FormError) = messages(formError.message, formError.args: _*)
  }

}
