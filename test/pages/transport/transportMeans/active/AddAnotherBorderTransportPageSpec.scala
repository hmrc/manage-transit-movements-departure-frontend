package pages.transport.transportMeans.active

import pages.behaviours.PageBehaviours

class AddAnotherBorderTransportPageSpec extends PageBehaviours {

  "AddAnotherBorderTransportPage" - {

    beRetrievable[Boolean](AddAnotherBorderTransportPage)

    beSettable[Boolean](AddAnotherBorderTransportPage)

    beRemovable[Boolean](AddAnotherBorderTransportPage)
  }
}
