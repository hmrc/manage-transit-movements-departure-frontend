package controllers.transport.transportMeans.active

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.transport.TransportMeansNavigatorProvider
import pages.transport.transportMeans.active.AddAnotherBorderTransportPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.transportMeans.active.AddAnotherBorderTransportView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherBorderTransportController @Inject()(
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TransportMeansNavigatorProvider,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherBorderTransportView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider("transport.transportMeans.active.addAnotherBorderTransport")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AddAnotherBorderTransportPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, lrn, mode))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode))),
        value => {
          implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
          AddAnotherBorderTransportPage.writeToUserAnswers(value).writeToSession().navigate()
        }
      )
  }
}
