package pages.transport.authorisationsAndLimit.limit

import pages.behaviours.PageBehaviours

import java.time.LocalDate

class LimitDatePageSpec extends PageBehaviours {

  "LimitDatePage" - {

    beRetrievable[LocalDate](LimitDatePage)

    beSettable[LocalDate](LimitDatePage)

    beRemovable[LocalDate](LimitDatePage)
  }
}
