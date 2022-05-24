package controllers.$package$

import controllers.actions._
import forms.$formProvider$
import javax.inject.Inject
import models.{Mode, LocalReferenceNumber}
import navigation.Navigator
import navigation.annotations.$navRoute$
import pages.$package$.$className$Page
import pages.$package$.$addressHolderNamePage$
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
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: $formProvider$,
  val controllerComponents: MessagesControllerComponents,
  view: $className$View
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[_]

  private def name(implicit request: Request): String = request.arg

  private def form(implicit request: Request): Form[Address] =
    formProvider("$package$.$className;format="decap"$", name)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage($addressHolderNamePage$)) {
      implicit request =>
        val preparedForm = request.userAnswers.get($className$Page) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, lrn, mode, name))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage($addressHolderNamePage$))
    .async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, name))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set($className$Page, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage($className$Page, mode, updatedAnswers))
          )
    }
}
