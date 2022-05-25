package pages.$package$

import pages.behaviours.PageBehaviours
import models.$objectClassName$

class $className$PageSpec extends PageBehaviours {

  "$className$Page" - {

    beRetrievable[$objectClassName$]($className$Page)

    beSettable[$objectClassName$]($className$Page)

    beRemovable[$objectClassName$]($className$Page)
  }
}
