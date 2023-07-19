package controllers

import controllers.actions.{Actions, IdentifierAction}
import forms.preTaskList.LocalReferenceNumberFormProvider
import models.LocalReferenceNumber
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.DuplicateService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.NewLocalReferenceNumberView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DraftIndexController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      identify: IdentifierAction,
                                      val controllerComponents: MessagesControllerComponents,
                                      service: DuplicateService
                                    )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport {


  def index(lrn: LocalReferenceNumber): Action[AnyContent] = identify.async {
   implicit request =>
     service.doesSubmissionExistForLrn(lrn).map {
      case true  => Redirect(???)
      case false => Redirect(controllers.routes.RedirectController.onPageLoad())
    }
  }


}
