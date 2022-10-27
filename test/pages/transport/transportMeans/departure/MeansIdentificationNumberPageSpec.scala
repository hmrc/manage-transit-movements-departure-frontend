package pages.transport.transportMeans.departure

import pages.behaviours.PageBehaviours

class MeansIdentificationNumberPageSpec extends PageBehaviours {

  "MeansIdentificationNumberPage" - {

    beRetrievable[String](MeansIdentificationNumberPage)

    beSettable[String](MeansIdentificationNumberPage)

    beRemovable[String](MeansIdentificationNumberPage)
  }
}
