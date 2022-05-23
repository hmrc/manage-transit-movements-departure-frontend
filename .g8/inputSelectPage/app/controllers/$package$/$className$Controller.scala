package controllers.$package$

import controllers.actions._
import forms.$package$.$className$FormProvider
import javax.inject.Inject
import models.{Mode, LocalReferenceNumber}
import navigation.Navigator
import navigation.annotations.$navRoute$
import pages.$package$.$className$Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.$package$.$className$View
import services.$serviceName$
import scala.concurrent.{ExecutionContext, Future}

class $className$Controller @Inject()(
   override val messagesApi: MessagesApi,
   sessionRepository: SessionRepository,
   @$navRoute$ navigator: Navigator,
   actions: Actions,
   formProvider: $className$FormProvider,
   service: $serviceName$,
   val controllerComponents: MessagesControllerComponents,
   view: $className$View
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.$lookupReferenceListMethod$.map {
        $referenceListClass;format="decap"$ =>
          val form = formProvider("$package$.$className;format="decap"$", $referenceListClass;format="decap"$)
          val preparedForm = request.userAnswers.get($className$Page) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, lrn, $referenceListClass;format="decap"$.$referenceClassPlural;format="decap"$, mode))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.$lookupReferenceListMethod$.flatMap {
        $referenceListClass;format="decap"$ =>
          val form = formProvider("$package$.$className;format="decap"$", $referenceListClass;format="decap"$)
          form.bindFromRequest().fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, $referenceListClass;format="decap"$.$referenceClassPlural;format="decap"$, mode))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set($className$Page, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage($className$Page, mode, updatedAnswers))
          )
      }
  }
}
