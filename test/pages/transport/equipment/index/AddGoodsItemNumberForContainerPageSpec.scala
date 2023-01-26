package pages.transport.equipment.index

import pages.behaviours.PageBehaviours

class AddGoodsItemNumberForContainerPageSpec extends PageBehaviours {

  "AddGoodsItemNumberForContainerPage" - {

    beRetrievable[Boolean](AddGoodsItemNumberForContainerPage)

    beSettable[Boolean](AddGoodsItemNumberForContainerPage)

    beRemovable[Boolean](AddGoodsItemNumberForContainerPage)
  }
}
