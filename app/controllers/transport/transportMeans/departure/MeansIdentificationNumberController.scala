package controllers.transport.transportMeans.departure

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.MeansIdentificationNumberProvider
import models.{LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.transport.TransportMeansNavigatorProvider
import pages.transport.transportMeans.departure.{InlandModePage, MeansIdentificationNumberPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.transportMeans.departure.MeansIdentificationNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MeansIdentificationNumberController @Inject()(
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TransportMeansNavigatorProvider,
  formProvider: MeansIdentificationNumberProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  getMandatoryPage: SpecificDataRequiredActionProvider,,
  view: MeansIdentificationNumberView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {



  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(InlandModePage)) { // TODO: Get identification of mode page
    implicit request =>
      val identificationType = request.arg.toString // TODO: Get identification arg [Might have to reshape data with toString]
      val form = formProvider("transport.transportMeans.departure.meansIdentificationNumber", identificationType)
      val preparedForm = request.userAnswers.get(MeansIdentificationNumberPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, lrn, mode, identificationType))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(InlandModePage))  // TODO: Get identification of mode page
    .async {
    implicit request =>
      val identificationType = request.arg.toString // TODO: Get identification arg
      val form = formProvider("transport.transportMeans.departure.meansIdentificationNumber", identificationType)
      form
       .bindFromRequest()
       .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, identificationType))),
        value => {
          implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
          MeansIdentificationNumberPage.writeToUserAnswers(value).writeToSession().navigate()
        }
    )
  }
}
