package pages.transport.transportMeans.active

import models.reference.CustomsOffice
import pages.behaviours.PageBehaviours

class CustomsOfficeActiveBorderPageSpec extends PageBehaviours {

  "CustomsOfficeActiveBorderPage" - {

    beRetrievable[CustomsOffice](CustomsOfficeActiveBorderPage)

    beSettable[CustomsOffice](CustomsOfficeActiveBorderPage)

    beRemovable[CustomsOffice](CustomsOfficeActiveBorderPage)
  }
}
