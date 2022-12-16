package pages.transport.supplyChainActors

import pages.behaviours.PageBehaviours

class IdentificationNumberPageSpec extends PageBehaviours {

  "IdentificationNumberPage" - {

    beRetrievable[String](IdentificationNumberPage)

    beSettable[String](IdentificationNumberPage)

    beRemovable[String](IdentificationNumberPage)
  }
}
