package pages.$package$

import pages.behaviours.PageBehaviours

class $className$PageSpec extends PageBehaviours {

  "$package$.$className$Page" - {

    beRetrievable[Boolean]($className$Page)

    beSettable[Boolean]($className$Page)

    beRemovable[Boolean]($className$Page)
  }
}
