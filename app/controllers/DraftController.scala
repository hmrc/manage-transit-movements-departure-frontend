package controllers

import controllers.actions.{Actions, IdentifierAction}
import models.LocalReferenceNumber
import models.domain.UserAnswersReader
import models.journeyDomain.PreTaskListDomain
import navigation.PreTaskListNavigatorProvider
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import models.{DeclarationType, NormalMode}

import javax.inject.Inject

class DraftController @Inject() (
                                  val controllerComponents: MessagesControllerComponents,
                                  actions: Actions,
                                  navigatorProvider: PreTaskListNavigatorProvider,
                                ) extends FrontendBaseController{



  def draftRedirect(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      UserAnswersReader[PreTaskListDomain].run(request.userAnswers) match {
        case Left(value)  => navigatorProvider(NormalMode).nextPage(request.userAnswers) // TODO Turn into result....
        case Right(value) => Redirect(???) // TODO task list
      }

  }
}
