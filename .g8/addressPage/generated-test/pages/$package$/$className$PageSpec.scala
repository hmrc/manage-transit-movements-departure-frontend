package pages.$package$

import pages.behaviours.PageBehaviours
import models.Address

class $className$PageSpec extends PageBehaviours {

  "$className$Page" - {

    beRetrievable[Address]($className$Page)

    beSettable[Address]($className$Page)

    beRemovable[Address]($className$Page)
  }
}
