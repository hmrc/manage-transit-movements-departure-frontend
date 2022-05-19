package controllers.$package$

import controllers.actions._
import forms.$package$.$className$FormProvider
import javax.inject.Inject
import models.{Mode, LocalReferenceNumber}
import navigation.Navigator
import navigation.annotations.$navRoute$
import pages.$className$Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.$package$.$className$View

import scala.concurrent.{ExecutionContext, Future}

class $className;format="cap"$Controller @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    @$navRoute$ navigator: Navigator,
    actions: Actions,
    formProvider: $className$FormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: $className$View
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>

      val preparedForm = request.userAnswers.get($className$Page) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, lrn, mode))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set($className$Page, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage($className$Page, mode, updatedAnswers))
      )
  }
}
