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

package models.journeyDomain.transport.equipment

import base.SpecBase
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.transport.equipment.index.itemNumber.{ItemNumberDomain, ItemNumbersDomain}
import models.journeyDomain.transport.equipment.seal.{SealDomain, SealsDomain}
import models.transport.authorisations.AuthorisationType
import models.{Index, ProcedureType}
import pages.preTaskList.ProcedureTypePage
import pages.sections.transport.equipment.EquipmentSection
import pages.transport.authorisationsAndLimit.authorisations.index.AuthorisationTypePage
import pages.transport.equipment.index._
import pages.transport.equipment.index.itemNumber.ItemNumberPage
import pages.transport.equipment.index.seals.IdentificationNumberPage
import pages.transport.preRequisites.ContainerIndicatorPage
import play.api.libs.json.Json

class EquipmentDomainSpec extends SpecBase with Generators {

  private val containerId     = nonEmptyString.sample.value
  private val sealId          = nonEmptyString.sample.value
  private val goodsItemNumber = nonEmptyString.sample.value

  "Equipment domain" - {

    "containerIdReads" - {
      "can be read from user answers" - {
        "when at index 0" in {
          val index = Index(0)

          val userAnswers = emptyUserAnswers
            .setValue(ContainerIdentificationNumberPage(index), containerId)

          val expectedResult = Some(containerId)

          val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
            EquipmentDomain.containerIdReads(index)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

        "when not at index 0" - {
          val index = Index(1)

          "and container indicator is true" - {
            "and add container id yes/no is true" in {
              val userAnswers = emptyUserAnswers
                .setValue(ContainerIndicatorPage, true)
                .setValue(EquipmentSection(Index(0)), Json.obj("foo" -> "bar"))
                .setValue(AddContainerIdentificationNumberYesNoPage(index), true)
                .setValue(ContainerIdentificationNumberPage(index), containerId)

              val expectedResult = Some(containerId)

              val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                EquipmentDomain.containerIdReads(index)
              ).run(userAnswers)

              result.value mustBe expectedResult
            }

            "and add container id yes/no is false" in {
              val userAnswers = emptyUserAnswers
                .setValue(ContainerIndicatorPage, true)
                .setValue(EquipmentSection(Index(0)), Json.obj("foo" -> "bar"))
                .setValue(AddContainerIdentificationNumberYesNoPage(index), false)

              val expectedResult = None

              val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                EquipmentDomain.containerIdReads(index)
              ).run(userAnswers)

              result.value mustBe expectedResult
            }
          }

          "and container indicator is false" in {
            val userAnswers = emptyUserAnswers
              .setValue(ContainerIndicatorPage, false)

            val expectedResult = None

            val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
              EquipmentDomain.containerIdReads(index)
            ).run(userAnswers)

            result.value mustBe expectedResult
          }
        }
      }

      "cannot be read from user answers" - {
        "when at index 0" - {
          val index = Index(0)

          "and container id is unanswered" in {
            val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
              EquipmentDomain.containerIdReads(index)
            ).run(emptyUserAnswers)

            result.left.value.page mustBe ContainerIdentificationNumberPage(index)
          }
        }

        "when not at index 0" - {
          val index = Index(1)

          "and container indicator is true" - {
            "and add container id yes/no is unanswered" in {
              val userAnswers = emptyUserAnswers
                .setValue(ContainerIndicatorPage, true)

              val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                EquipmentDomain.containerIdReads(index)
              ).run(userAnswers)

              result.left.value.page mustBe AddContainerIdentificationNumberYesNoPage(index)
            }

            "and add container id yes/no is true" - {
              "and container id is unanswered" in {
                val userAnswers = emptyUserAnswers
                  .setValue(ContainerIndicatorPage, true)
                  .setValue(EquipmentSection(Index(0)), Json.obj("foo" -> "bar"))
                  .setValue(AddContainerIdentificationNumberYesNoPage(index), true)

                val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                  EquipmentDomain.containerIdReads(index)
                ).run(userAnswers)

                result.left.value.page mustBe ContainerIdentificationNumberPage(index)
              }
            }
          }
        }
      }
    }

    "sealsReads" - {
      "can be read from user answers" - {
        "when add seals yes/no is no" in {
          val userAnswers = emptyUserAnswers
            .setValue(ProcedureTypePage, ProcedureType.Normal)
            .setValue(AddSealYesNoPage(equipmentIndex), false)

          val expectedResult = None

          val result: EitherType[Option[SealsDomain]] = UserAnswersReader[Option[SealsDomain]](
            EquipmentDomain.sealsReads(equipmentIndex)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

        "when add seals yes/no is yes" in {
          val userAnswers = emptyUserAnswers
            .setValue(ProcedureTypePage, ProcedureType.Normal)
            .setValue(AddSealYesNoPage(equipmentIndex), true)
            .setValue(IdentificationNumberPage(equipmentIndex, sealIndex), sealId)

          val expectedResult = Some(
            SealsDomain(
              Seq(
                SealDomain(sealId)(equipmentIndex, sealIndex)
              )
            )
          )

          val result: EitherType[Option[SealsDomain]] = UserAnswersReader[Option[SealsDomain]](
            EquipmentDomain.sealsReads(equipmentIndex)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }
      }

      "cannot be read from user answers" - {
        "when simplified procedure type and any authorisation has SSE type" - {
          "and seal identification number unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(ProcedureTypePage, ProcedureType.Simplified)
              .setValue(AuthorisationTypePage(Index(0)), AuthorisationType.ACR)
              .setValue(AuthorisationTypePage(Index(1)), AuthorisationType.SSE)

            val result: EitherType[Option[SealsDomain]] = UserAnswersReader[Option[SealsDomain]](
              EquipmentDomain.sealsReads(equipmentIndex)
            ).run(userAnswers)

            result.left.value.page mustBe IdentificationNumberPage(equipmentIndex, Index(0))
          }
        }

        "when simplified procedure type and no authorisation has SSE type" - {
          "and add seals yes/no is unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(ProcedureTypePage, ProcedureType.Simplified)
              .setValue(AuthorisationTypePage(Index(0)), AuthorisationType.ACR)
              .setValue(AuthorisationTypePage(Index(1)), AuthorisationType.TRD)

            val result: EitherType[Option[SealsDomain]] = UserAnswersReader[Option[SealsDomain]](
              EquipmentDomain.sealsReads(equipmentIndex)
            ).run(userAnswers)

            result.left.value.page mustBe AddSealYesNoPage(equipmentIndex)
          }
        }

        "when not a simplified procedure type and any authorisation has SSE type" - {
          "and add seals yes/no is unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(ProcedureTypePage, ProcedureType.Normal)
              .setValue(AuthorisationTypePage(Index(0)), AuthorisationType.SSE)
              .setValue(AuthorisationTypePage(Index(1)), AuthorisationType.ACR)

            val result: EitherType[Option[SealsDomain]] = UserAnswersReader[Option[SealsDomain]](
              EquipmentDomain.sealsReads(equipmentIndex)
            ).run(userAnswers)

            result.left.value.page mustBe AddSealYesNoPage(equipmentIndex)
          }
        }

        "when simplified procedure type and no authorisation has SSE type" - {
          "and seal identification number unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(ProcedureTypePage, ProcedureType.Normal)
              .setValue(AuthorisationTypePage(Index(0)), AuthorisationType.ACR)
              .setValue(AuthorisationTypePage(Index(1)), AuthorisationType.TRD)

            val result: EitherType[Option[SealsDomain]] = UserAnswersReader[Option[SealsDomain]](
              EquipmentDomain.sealsReads(equipmentIndex)
            ).run(userAnswers)

            result.left.value.page mustBe AddSealYesNoPage(equipmentIndex)
          }
        }
      }
    }

    "goodItemNumberReads" - {
      "can be read form user answers" - {
        "when add seals yes/no is no" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddSealYesNoPage(equipmentIndex), false)

          val expectedResult = None

          val result: EitherType[Option[ItemNumbersDomain]] = UserAnswersReader[Option[ItemNumbersDomain]](
            EquipmentDomain.goodItemNumberReads(index)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

        "when add seals yes/no is yes" - {
          "and at index 0" - {
            val index = Index(0)

            "and container id is answered" - {
              "and add goods item numbers yes/no is no" in {
                val userAnswers = emptyUserAnswers
                  .setValue(ContainerIdentificationNumberPage(index), containerId)
                  .setValue(AddSealYesNoPage(index), true)
                  .setValue(AddGoodsItemNumberYesNoPage(index), false)

                val expectedResult = None

                val result: EitherType[Option[ItemNumbersDomain]] = UserAnswersReader[Option[ItemNumbersDomain]](
                  EquipmentDomain.goodItemNumberReads(index)
                ).run(userAnswers)

                result.value mustBe expectedResult
              }

              "and add goods item numbers yes/no is yes" in {
                val userAnswers = emptyUserAnswers
                  .setValue(ContainerIdentificationNumberPage(index), containerId)
                  .setValue(AddSealYesNoPage(index), true)
                  .setValue(AddGoodsItemNumberYesNoPage(index), true)
                  .setValue(ItemNumberPage(index, itemNumberIndex), goodsItemNumber)

                val expectedResult = Some(
                  ItemNumbersDomain(
                    Seq(
                      ItemNumberDomain(goodsItemNumber)(index, itemNumberIndex)
                    )
                  )
                )

                val result: EitherType[Option[ItemNumbersDomain]] = UserAnswersReader[Option[ItemNumbersDomain]](
                  EquipmentDomain.goodItemNumberReads(index)
                ).run(userAnswers)

                result.value mustBe expectedResult
              }
            }

            "and container id is unanswered" in {
              val userAnswers = emptyUserAnswers
                .setValue(AddSealYesNoPage(index), true)
                .setValue(ItemNumberPage(index, itemNumberIndex), goodsItemNumber)

              val expectedResult = Some(
                ItemNumbersDomain(
                  Seq(
                    ItemNumberDomain(goodsItemNumber)(index, itemNumberIndex)
                  )
                )
              )

              val result: EitherType[Option[ItemNumbersDomain]] = UserAnswersReader[Option[ItemNumbersDomain]](
                EquipmentDomain.goodItemNumberReads(index)
              ).run(userAnswers)

              result.value mustBe expectedResult
            }
          }

          "and not at index 0" - {
            val index = Index(1)

            "and container id is answered" in {
              val userAnswers = emptyUserAnswers
                .setValue(EquipmentSection(Index(0)), Json.obj("foo" -> "bar"))
                .setValue(ContainerIdentificationNumberPage(index), containerId)
                .setValue(AddSealYesNoPage(index), true)
                .setValue(ItemNumberPage(index, itemNumberIndex), goodsItemNumber)

              val expectedResult = Some(
                ItemNumbersDomain(
                  Seq(
                    ItemNumberDomain(goodsItemNumber)(index, itemNumberIndex)
                  )
                )
              )

              val result: EitherType[Option[ItemNumbersDomain]] = UserAnswersReader[Option[ItemNumbersDomain]](
                EquipmentDomain.goodItemNumberReads(index)
              ).run(userAnswers)

              result.value mustBe expectedResult
            }

            "and container id is unanswered" in {
              val userAnswers = emptyUserAnswers
                .setValue(EquipmentSection(Index(0)), Json.obj("foo" -> "bar"))
                .setValue(AddSealYesNoPage(index), true)
                .setValue(ItemNumberPage(index, itemNumberIndex), goodsItemNumber)

              val expectedResult = Some(
                ItemNumbersDomain(
                  Seq(
                    ItemNumberDomain(goodsItemNumber)(index, itemNumberIndex)
                  )
                )
              )

              val result: EitherType[Option[ItemNumbersDomain]] = UserAnswersReader[Option[ItemNumbersDomain]](
                EquipmentDomain.goodItemNumberReads(index)
              ).run(userAnswers)

              result.value mustBe expectedResult
            }
          }
        }
      }

      "cannot be read from user answers" - {
        "when add seals yes/no is unanswered" in {
          val result: EitherType[Option[ItemNumbersDomain]] = UserAnswersReader[Option[ItemNumbersDomain]](
            EquipmentDomain.goodItemNumberReads(index)
          ).run(emptyUserAnswers)

          result.left.value.page mustBe AddSealYesNoPage(equipmentIndex)
        }

        "when at index 0 and container id is answered" - {
          val index = Index(0)

          "and add goods item numbers yes/no is unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(ContainerIdentificationNumberPage(index), containerId)
              .setValue(AddSealYesNoPage(equipmentIndex), true)

            val result: EitherType[Option[ItemNumbersDomain]] = UserAnswersReader[Option[ItemNumbersDomain]](
              EquipmentDomain.goodItemNumberReads(index)
            ).run(userAnswers)

            result.left.value.page mustBe AddGoodsItemNumberYesNoPage(equipmentIndex)
          }
        }
      }
    }
  }
}
