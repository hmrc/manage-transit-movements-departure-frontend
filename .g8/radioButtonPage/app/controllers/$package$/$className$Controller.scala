package controllers.$package$

import controllers.actions._
import forms.$package$.$formProvider$
import javax.inject.Inject
import models.{Mode, LocalReferenceNumber}
import models.$package$.$className$
import navigation.Navigator
import navigation.annotations.$navRoute$
import pages.$package$.$className$Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.$package$.$className$View
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}

import scala.concurrent.{ExecutionContext, Future}

class $className$Controller @Inject()(
   override val messagesApi: MessagesApi,
   implicit val sessionRepository: SessionRepository,
   @$navRoute$ implicit val navigator: Navigator,
   actions: Actions,
   formProvider: $formProvider$,
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

      Ok(view(preparedForm, lrn, $className$.radioItems, mode))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, $className$.radioItems, mode))),
        value => $className$Page.writeToUserAnswers(value).writeToSession().navigateWith(mode)
      )
  }
}
