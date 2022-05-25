package pages.traderDetails.holderOfTransit

import pages.behaviours.PageBehaviours

class TirIdentificationNoControllerPageSpec extends PageBehaviours {

  "TirIdentificationNoControllerPage" - {

    beRetrievable[String](TirIdentificationNoControllerPage)

    beSettable[String](TirIdentificationNoControllerPage)

    beRemovable[String](TirIdentificationNoControllerPage)
  }
}
