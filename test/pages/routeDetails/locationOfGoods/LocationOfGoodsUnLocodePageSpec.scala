package pages.routeDetails.locationOfGoods

import models.reference.UnLocode
import pages.behaviours.PageBehaviours

class LocationOfGoodsUnLocodePageSpec extends PageBehaviours {

  "LocationOfGoodsUnLocodePage" - {

    beRetrievable[UnLocode](LocationOfGoodsUnLocodePage)

    beSettable[UnLocode](LocationOfGoodsUnLocodePage)

    beRemovable[UnLocode](LocationOfGoodsUnLocodePage)
  }
}
