package api

import generated._
import models.journeyDomain.PreTaskListDomain

object Conversions {

  def convert(preTaskListDomain: PreTaskListDomain): CC004CType = {
    val m1: MESSAGESequence = ???
    val to: TransitOperationType01 = ???
    val cod : CustomsOfficeOfDepartureType03 = ???
    val holder: HolderOfTheTransitProcedureType20 = ???

    CC004CType(m1, to, cod, holder)
  }

}
