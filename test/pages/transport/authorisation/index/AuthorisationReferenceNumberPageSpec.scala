package pages.transport.authorisation.index

import pages.behaviours.PageBehaviours

class AuthorisationReferenceNumberPageSpec extends PageBehaviours {

  "AuthorisationReferenceNumberPage" - {

    beRetrievable[String](AuthorisationReferenceNumberPage)

    beSettable[String](AuthorisationReferenceNumberPage)

    beRemovable[String](AuthorisationReferenceNumberPage)
  }
}
