package pages.transport.carrierDetails

import pages.behaviours.PageBehaviours

class AddContactYesNoPageSpec extends PageBehaviours {

  "AddContactYesNoPage" - {

    beRetrievable[Boolean](AddContactYesNoPage)

    beSettable[Boolean](AddContactYesNoPage)

    beRemovable[Boolean](AddContactYesNoPage)
  }
}
