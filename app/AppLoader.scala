import actors.StatsActor
import actors.StatsActor.Ping
import akka.actor.{ActorRef, Props}
import com.softwaremill.macwire._
import controllers.{Application, AssetsComponents}
import filters.StatsFilter
import helpers.ActionRunner
import play.api
import play.api.ApplicationLoader.Context
import play.api._
import play.api.db.slick.{DatabaseConfigProvider, DbName, DefaultSlickApi, SlickApi, SlickComponents}
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import repositories.UserRepository
import router.Routes
import services.{SunService, WeatherService}
import slick.basic.BasicProfile

import scala.concurrent.Future

class AppApplicationLoader extends ApplicationLoader {
  def load(context: Context): api.Application = {
    LoggerConfigurator(context.environment.classLoader).foreach { configurator =>
      configurator.configure(context.environment)
    }
    new AppComponents(context).application
  }
}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with AhcWSComponents
  with AssetsComponents
  with HttpFiltersComponents
  with SlickComponents {

  private val log = Logger(this.getClass)

  lazy val statsActor: ActorRef = actorSystem.actorOf(Props(wire[StatsActor]), StatsActor.name)
  override lazy val controllerComponents: ControllerComponents = wire[DefaultControllerComponents]
  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  lazy val actionRunner: ActionRunner = wire[ActionRunner]
  lazy val userRepository: UserRepository = wire[UserRepository]
  lazy val applicationController: Application = wire[Application]

  override lazy val slickApi: SlickApi =
    new DefaultSlickApi(environment, configuration, applicationLifecycle)(executionContext)

  lazy val databaseConfigProvider: DatabaseConfigProvider = new DatabaseConfigProvider {
    override def get[P <: BasicProfile] = slickApi.dbConfig(DbName("default"))
  }

  lazy val sunService: SunService = wire[SunService]
  lazy val weatherService: WeatherService = wire[WeatherService]

  applicationLifecycle.addStopHook { () =>
    log.info("The app is about to stop")
    Future.successful(())
  }

  val onStart: Unit = {
    log.info("The app is about to start")
    statsActor ! Ping
  }

  lazy val statsFilter: Filter = wire[StatsFilter]
  override lazy val httpFilters = Seq(statsFilter)
}
