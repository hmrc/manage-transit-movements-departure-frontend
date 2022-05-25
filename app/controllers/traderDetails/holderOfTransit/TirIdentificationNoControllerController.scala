package controllers.traderDetails.holderOfTransit

import controllers.actions._
import forms.EoriNumberFormProvider
import models.{Mode, LocalReferenceNumber}
import navigation.Navigator
import navigation.annotations.TraderDetails
import pages.traderDetails.holderOfTransit.TirIdentificationNoControllerPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.traderDetails.holderOfTransit.TirIdentificationNoControllerView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TirIdentificationNoControllerController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    @TraderDetails navigator: Navigator,
    formProvider: EoriNumberFormProvider,
    actions: Actions,
    val controllerComponents: MessagesControllerComponents,
    view: TirIdentificationNoControllerView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider("traderDetails.holderOfTransit.tirIdentificationNoController")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TirIdentificationNoControllerPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, lrn, mode))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      form
       .bindFromRequest()
       .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TirIdentificationNoControllerPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TirIdentificationNoControllerPage, mode, updatedAnswers))
      )
  }
}
