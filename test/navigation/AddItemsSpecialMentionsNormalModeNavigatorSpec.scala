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

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.addItems.specialMentions.routes
import generators.Generators
import models.DeclarationType.{Option1, Option4}
import models.reference.CircumstanceIndicator
import models.{Index, NormalMode}
import navigation.annotations.addItemsNavigators.AddItemsSpecialMentionsNavigator
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.addItems.specialMentions._
import pages.addItems.{AddExtraDocumentInformationPage, DocumentExtraInformationPage, DocumentReferencePage, DocumentTypePage}
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}
import pages.{AddSecurityDetailsPage, DeclarationTypePage}

class AddItemsSpecialMentionsNormalModeNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {

  val navigator = new AddItemsSpecialMentionsNavigator

  "Special Mentions section" - {

    "in normal mode" - {

      "must go from AddSpecialMention" - {

        "SpecialMentionType when true" in {

          val userAnswers = emptyUserAnswers.set(AddSpecialMentionPage(itemIndex), true).success.value

          navigator
            .nextPage(AddSpecialMentionPage(itemIndex), NormalMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.lrn, itemIndex, referenceIndex, NormalMode))
        }

        "to TIRCarnetReference page when false and " +
          "its a TIR declaration type and " +
          "its the first document on the first item" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(DeclarationTypePage)(Option4)

            navigator
              .nextPage(AddSpecialMentionPage(itemIndex), NormalMode, userAnswers)
              .mustBe(controllers.addItems.documents.routes.TIRCarnetReferenceController.onPageLoad(userAnswers.lrn, itemIndex, itemIndex, NormalMode))
          }

        "to standard add document journey when false and " +
          "its a TIR declaration type and " +
          "its not the first document on the first item" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option4)

            navigator
              .nextPage(AddSpecialMentionPage(Index(1)), NormalMode, userAnswers)
              .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(userAnswers.lrn, Index(1), NormalMode))

          }

        "to standard add document journey when false and " +
          "its a TIR declaration type and " +
          "its the first document but not the first item" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(DocumentTypePage(itemIndex, referenceIndex))("documentType")
              .unsafeSetVal(DocumentReferencePage(itemIndex, referenceIndex))("documentReference")
              .unsafeSetVal(AddExtraDocumentInformationPage(itemIndex, referenceIndex))(true)
              .unsafeSetVal(DocumentExtraInformationPage(itemIndex, referenceIndex))("documentExtraInformation")
              .unsafeSetVal(DeclarationTypePage)(Option4)

            navigator
              .nextPage(AddSpecialMentionPage(itemIndex), NormalMode, userAnswers)
              .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(userAnswers.lrn, itemIndex, NormalMode))
          }

        "to AddDocuments when set to false and safety " +
          "and security is selected as 'No'" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option1)

            navigator
              .nextPage(AddSpecialMentionPage(itemIndex), NormalMode, userAnswers)
              .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(userAnswers.lrn, itemIndex, NormalMode))
          }

        "to DocumentType when set to false " +
          "and AddSecurityDetailsPage is 'Yes' " +
          "and  AddCircumstanceIndicatorPage is 'No' " +
          "and it is the first Item " +
          "and AddCommercialReferenceNumberPage is false" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option1)

            navigator
              .nextPage(AddSpecialMentionPage(itemIndex), NormalMode, userAnswers)
              .mustBe(controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(userAnswers.lrn, itemIndex, itemIndex, NormalMode))
          }

        "to AddDocumentType when set to false and AddSecurityDetailsPage is 'Yes' " +
          "and  AddCircumstanceIndicatorPage is 'No' and it is the not the first Item " +
          "and AddCommercialReferenceNumberPage is false" in {

            val nextIndex = Index(1)

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option1)

            navigator
              .nextPage(AddSpecialMentionPage(nextIndex), NormalMode, userAnswers)
              .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(userAnswers.lrn, nextIndex, NormalMode))
          }

        "to DocumentType when set to false and AddSecurityDetailsPage is 'Yes' and " +
          "AddCircumstanceIndicatorPage is 'Yes' and " +
          "CircumstanceIndicator is either E, D, C and B && AddCommercialReferenceNumberPage is false" in {

            val circumstanceIndicator = Gen.oneOf(CircumstanceIndicator.conditionalIndicators).sample.value

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)(circumstanceIndicator)
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option1)

            navigator
              .nextPage(AddSpecialMentionPage(itemIndex), NormalMode, userAnswers)
              .mustBe(controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(userAnswers.lrn, itemIndex, itemIndex, NormalMode))
          }

        "to DocumentType when set to false and AddSecurityDetailsPage is 'Yes' and " +
          "AddCircumstanceIndicatorPage is 'Yes' and " +
          "CircumstanceIndicator other then E, D, C and B" in {

            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
              .unsafeSetVal(CircumstanceIndicatorPage)("something")
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
              .unsafeSetVal(DeclarationTypePage)(Option1)

            navigator
              .nextPage(AddSpecialMentionPage(itemIndex), NormalMode, userAnswers)
              .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(userAnswers.lrn, itemIndex, NormalMode))
          }

      }

      "must go from SpecialMentionType to SpecialMentionAdditionalInfo" in {
        navigator
          .nextPage(SpecialMentionTypePage(itemIndex, referenceIndex), NormalMode, emptyUserAnswers)
          .mustBe(routes.SpecialMentionAdditionalInfoController.onPageLoad(emptyUserAnswers.lrn, itemIndex, referenceIndex, NormalMode))
      }

      "must go from SpecialMentionAdditionalInfo to CYA" in {
        navigator
          .nextPage(SpecialMentionAdditionalInfoPage(itemIndex, referenceIndex), NormalMode, emptyUserAnswers)
          .mustBe(routes.SpecialMentionCheckYourAnswersController.onPageLoad(emptyUserAnswers.lrn, itemIndex, referenceIndex, NormalMode))
      }

      "must go from RemoveSpecialMentionController" - {

        "to AddAnotherSpecialMentionController when at least one special mention exists" in {

          val userAnswers = emptyUserAnswers
            .set(SpecialMentionTypePage(itemIndex, referenceIndex), "value")
            .success
            .value

          navigator
            .nextPage(RemoveSpecialMentionPage(itemIndex, referenceIndex), NormalMode, userAnswers)
            .mustBe(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, NormalMode))
        }

        "to AddSpecialMentionPage when no special mentions exist" in {
          navigator
            .nextPage(RemoveSpecialMentionPage(itemIndex, referenceIndex), NormalMode, emptyUserAnswers)
            .mustBe(routes.AddSpecialMentionController.onPageLoad(emptyUserAnswers.lrn, itemIndex, NormalMode))
        }
      }

      "must go from AddAnotherSpecialMention" - {

        "to SpecialMentionType when set to true" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddAnotherSpecialMentionPage(itemIndex))(true)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(itemIndex), NormalMode, userAnswers)
            .mustBe(routes.SpecialMentionTypeController.onPageLoad(userAnswers.lrn, itemIndex, referenceIndex, NormalMode))
        }

        "to AddDocuments when set to false and safeTye and security is selected as 'No'" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddSecurityDetailsPage)(false)
            .unsafeSetVal(AddAnotherSpecialMentionPage(itemIndex))(false)
            .unsafeSetVal(DeclarationTypePage)(Option1)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(itemIndex), NormalMode, userAnswers)
            .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(userAnswers.lrn, itemIndex, NormalMode))
        }

        "to DocumentType when set to false and AddSecurityDetailsPage is 'Yes' and  AddCircumstanceIndicatorPage is 'No' and it is the first Item and AddCommercialReferenceNumberPage is false" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddAnotherSpecialMentionPage(itemIndex))(false)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(DeclarationTypePage)(Option1)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(itemIndex), NormalMode, userAnswers)
            .mustBe(controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(userAnswers.lrn, itemIndex, itemIndex, NormalMode))
        }

        "to AddDocumentType when set to false and AddSecurityDetailsPage is 'Yes' and  AddCircumstanceIndicatorPage is 'No' and it is the not the first Item and AddCommercialReferenceNumberPage is false" in {
          val nextIndex = Index(1)

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddAnotherSpecialMentionPage(itemIndex))(false)
            .unsafeSetVal(AddAnotherSpecialMentionPage(nextIndex))(false)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(DeclarationTypePage)(Option1)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(nextIndex), NormalMode, userAnswers)
            .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(userAnswers.lrn, nextIndex, NormalMode))
        }

        "to DocumentType when set to false and AddSecurityDetailsPage is 'Yes' and  AddCircumstanceIndicatorPage is 'Yes' and CircumstanceIndicator is either E, D, C and B && AddCommercialReferenceNumberPage is false" in {

          val circumstanceIndicator = Gen.oneOf(CircumstanceIndicator.conditionalIndicators).sample.value

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
            .unsafeSetVal(CircumstanceIndicatorPage)(circumstanceIndicator)
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddAnotherSpecialMentionPage(itemIndex))(false)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(DeclarationTypePage)(Option1)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(itemIndex), NormalMode, userAnswers)
            .mustBe(controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(userAnswers.lrn, itemIndex, itemIndex, NormalMode))
        }

        "to DocumentType when set to false and AddSecurityDetailsPage is 'Yes' and  AddCircumstanceIndicatorPage is 'Yes' and CircumstanceIndicator other then E, D, C and B" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(AddCircumstanceIndicatorPage)(true)
            .unsafeSetVal(CircumstanceIndicatorPage)("something")
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(AddAnotherSpecialMentionPage(itemIndex))(false)
            .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
            .unsafeSetVal(DeclarationTypePage)(Option1)

          navigator
            .nextPage(AddAnotherSpecialMentionPage(itemIndex), NormalMode, userAnswers)
            .mustBe(controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(userAnswers.lrn, itemIndex, NormalMode))
        }
      }
    }
  }
}
