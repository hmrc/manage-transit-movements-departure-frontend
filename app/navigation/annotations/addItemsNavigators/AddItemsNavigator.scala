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

package navigation.annotations.addItemsNavigators

import controllers.{routes => mainRoutes}
import derivable._
import models._
import navigation.Navigator
import pages._
import pages.addItems._
import play.api.mvc.Call
import javax.inject.{Inject, Singleton}

@Singleton
class AddItemsNavigator @Inject() () extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case ConfirmRemoveItemPage => ua => Some(removeItem(NormalMode)(ua))
    case AddAnotherItemPage    => ua => Some(addAnotherItemRoute(ua))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {

    case ConfirmRemoveItemPage => ua => Some(removeItem(CheckMode)(ua))
    case AddAnotherItemPage    => ua => Some(addAnotherItemRoute(ua))
  }

  private def addAnotherItemRoute(userAnswers: UserAnswers): Call = {
    val count = userAnswers.get(DeriveNumberOfItems).getOrElse(0)
    userAnswers.get(AddAnotherItemPage) match {
      case Some(true) => controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(userAnswers.lrn, Index(count), NormalMode)
      case _          => mainRoutes.DeclarationSummaryController.onPageLoad(userAnswers.lrn)
    }
  }

  private def removeItem(mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfItems) match {
      case None | Some(0) => controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(ua.lrn, Index(0), mode)
      case _              => controllers.addItems.routes.AddAnotherItemController.onPageLoad(ua.lrn)
    }

}
