package pages.$package$

import pages.behaviours.PageBehaviours

class $className$PageSpec extends PageBehaviours {

  "$package$.$className$Page" - {

    beRetrievable[Int]($className$Page)

    beSettable[Int]($className$Page)

    beRemovable[Int]($className$Page)
  }
}
