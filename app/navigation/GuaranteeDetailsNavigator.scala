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

package navigation

import cats.implicits._
import config.FrontendAppConfig
import controllers.guaranteeDetails.routes
import derivable.DeriveNumberOfGuarantees
import models.DeclarationType.Option4
import models.GuaranteeType._
import models._
import models.reference.CountryCode
import pages.guaranteeDetails._
import pages.routeDetails.DestinationOfficePage
import pages._
import play.api.mvc.Call
import javax.inject.{Inject, Singleton}

@Singleton
class GuaranteeDetailsNavigator @Inject() (config: FrontendAppConfig) extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddAnotherGuaranteePage                  => ua => addAnotherGuaranteeRoute(ua)
    case ConfirmRemoveGuaranteePage               => ua => confirmRemoveGuaranteeRoute(ua)
    case GuaranteeTypePage(index)                 => ua => guaranteeTypeRoute(ua, index, NormalMode)
    case OtherReferencePage(index)                => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case GuaranteeReferencePage(index)            => ua => guaranteeReferenceNormalRoutes(ua, index, NormalMode)
    case TIRGuaranteeReferencePage(_)             => ua => Some(routes.AddAnotherGuaranteeController.onPageLoad(ua.lrn))
    case LiabilityAmountPage(index)               => ua => Some(routes.AccessCodeController.onPageLoad(ua.lrn, index, NormalMode))
    case OtherReferenceLiabilityAmountPage(index) => ua => otherReferenceLiablityAmountRoute(ua, index, NormalMode)
    case DefaultAmountPage(index)                 => ua => defaultAmountRoute(ua, index, NormalMode)
    case AccessCodePage(index)                    => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.lrn, index))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddAnotherGuaranteePage                  => ua => addAnotherGuaranteeRoute(ua)
    case ConfirmRemoveGuaranteePage               => ua => confirmRemoveGuaranteeRoute(ua)
    case GuaranteeTypePage(index)                 => ua => guaranteeTypeRoute(ua, index, CheckMode)
    case OtherReferencePage(index)                => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case GuaranteeReferencePage(index)            => ua => guaranteeReferenceRoutes(ua, index)
    case TIRGuaranteeReferencePage(_)             => ua => Some(routes.AddAnotherGuaranteeController.onPageLoad(ua.lrn))
    case LiabilityAmountPage(index)               => ua => liabilityAmountRoute(ua, index, CheckMode)
    case OtherReferenceLiabilityAmountPage(index) => ua => otherReferenceLiablityAmountRoute(ua, index, CheckMode)
    case DefaultAmountPage(index)                 => ua => defaultAmountRoute(ua, index, CheckMode)
    case AccessCodePage(index)                    => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    case _                                        => ua => Some(routes.AddAnotherGuaranteeController.onPageLoad(ua.lrn))
  }

  def otherReferenceLiablityAmountRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(LiabilityAmountPage(index)), ua.get(AccessCodePage(index)), mode) match {
      case (Some(x), _, _) if x.toDouble == 0.00 || x.toDouble.equals(0.0) || x.toDouble.toInt.equals(0) =>
        Some(routes.DefaultAmountController.onPageLoad(ua.lrn, index, mode))
      case (Some(_), _, NormalMode)      => Some(routes.AccessCodeController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(_), Some(_), CheckMode) => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.lrn, index))
      case (Some(_), None, CheckMode)    => Some(routes.AccessCodeController.onPageLoad(ua.lrn, index, CheckMode))
    }

  def guaranteeReferenceNormalRoutes(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(OfficeOfDeparturePage), ua.get(DestinationOfficePage)) match {
      case (Some(departureOffice), Some(destinationOffice))
          if departureOffice.countryId == CountryCode("GB") && (destinationOffice.countryId == CountryCode("GB")) =>
        Some(routes.LiabilityAmountController.onPageLoad(ua.lrn, index, mode))
      case _ => Some(routes.OtherReferenceLiabilityAmountController.onPageLoad(ua.lrn, index, mode))
    }

  def guaranteeReferenceRoutes(ua: UserAnswers, index: Index) =
    (ua.get(LiabilityAmountPage(index)), ua.get(AccessCodePage(index))) match {
      case (None, _) => guaranteeReferenceNormalRoutes(ua: UserAnswers, index, CheckMode)
      case (_, None) => Some(routes.AccessCodeController.onPageLoad(ua.lrn, index, CheckMode))
      case _         => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    }

  def liabilityAmountRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(GuaranteeTypePage(index)), ua.get(LiabilityAmountPage(index)), ua.get(AccessCodePage(index)), mode) match {
      case (Some(_), Some(""), _, NormalMode) => Some(routes.DefaultAmountController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(_), Some(_), _, NormalMode)  => Some(routes.AccessCodeController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(_), None, _, NormalMode)     => Some(routes.DefaultAmountController.onPageLoad(ua.lrn, index, NormalMode))
      case (_, _, None, CheckMode)            => Some(routes.AccessCodeController.onPageLoad(ua.lrn, index, CheckMode))
      case _                                  => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    }

  def defaultAmountRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(DefaultAmountPage(index)), ua.get(AccessCodePage(index)), mode) match {
      case (Some(true), _, NormalMode)   => Some(routes.AccessCodeController.onPageLoad(ua.lrn, index, mode))
      case (Some(true), None, CheckMode) => Some(routes.AccessCodeController.onPageLoad(ua.lrn, index, mode))
      case (Some(false), _, _)           => Some(routes.OtherReferenceLiabilityAmountController.onPageLoad(ua.lrn, index, mode))
      case _                             => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    }

  def addAnotherGuaranteeRoute(ua: UserAnswers): Option[Call] = {
    val count               = ua.get(DeriveNumberOfGuarantees).getOrElse(1)
    val declarationType     = ua.get(DeclarationTypePage)
    val addAnotherGuarantee = ua.get(AddAnotherGuaranteePage)

    if (count >= config.maxGuarantees)
      Some(controllers.routes.DeclarationSummaryController.onPageLoad(ua.lrn))
    else {
      (declarationType, addAnotherGuarantee).tupled.map {
        case (Option4, true) => routes.TIRGuaranteeReferenceController.onPageLoad(ua.lrn, Index(count), NormalMode)
        case (_, true)       => routes.GuaranteeTypeController.onPageLoad(ua.lrn, Index(count), NormalMode)
        case (_, false)      => controllers.routes.DeclarationSummaryController.onPageLoad(ua.lrn)
      }
    }
  }

  def confirmRemoveGuaranteeRoute(ua: UserAnswers): Option[Call] = {
    val count = ua.get(DeriveNumberOfGuarantees).getOrElse(0)

    ua.get(ConfirmRemoveGuaranteePage).map {
      case true if count == 0 => routes.GuaranteeTypeController.onPageLoad(ua.lrn, Index(count), NormalMode)
      case _                  => routes.AddAnotherGuaranteeController.onPageLoad(ua.lrn)
    }
  }

  def guaranteeTypeRoute(ua: UserAnswers, index: Index, mode: Mode): Option[Call] =
    (ua.get(GuaranteeTypePage(index)), ua.get(GuaranteeReferencePage(index)), ua.get(OtherReferencePage(index)), mode) match {
      case (Some(guaranteeType), _, _, NormalMode) if guaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeReferenceController.onPageLoad(ua.lrn, index, NormalMode))
      case (Some(guaranteeType), None, _, NormalMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.OtherReferenceController.onPageLoad(ua.lrn, index, NormalMode))

      case (Some(guaranteeType), Some(_), _, CheckMode) if guaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.lrn, index))

      case (Some(guaranteeType), _, Some(_), CheckMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.lrn, index))

      case (Some(guaranteeType), _, None, CheckMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.OtherReferenceController.onPageLoad(ua.lrn, index, CheckMode))

      case (Some(guaranteeType), None, None, CheckMode) if guaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeReferenceController.onPageLoad(ua.lrn, index, CheckMode))

      case _ => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    }

}
