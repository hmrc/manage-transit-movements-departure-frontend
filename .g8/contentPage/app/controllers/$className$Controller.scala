package controllers

import controllers.actions._
import javax.inject.Inject
import models.{LocalReferenceNumber}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.$className$View

import scala.concurrent.{ExecutionContext, Future}

class $className;format="cap"$Controller @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    actions: Actions,
    val controllerComponents: MessagesControllerComponents,
    view: $className$View
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      Ok(view(lrn))
  }
}
