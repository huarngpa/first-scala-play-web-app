package controllers

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

import actors.StatsActor
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import controllers.Assets.Asset
import helpers.ActionRunner
import model.CombinedData
import play.api.libs.json.Json
import play.api.mvc._
import repositories.UserRepository
import services.{SunService, WeatherService}

import scala.concurrent.ExecutionContext.Implicits.global

class Application(components: ControllerComponents,
                  actionRunner: ActionRunner,
                  userRepository: UserRepository,
                  assets: Assets,
                  sunService: SunService,
                  weatherService: WeatherService,
                  actorSystem: ActorSystem)
    extends AbstractController(components) {

  def index: Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def data: Action[AnyContent] = Action.async {
    val date = new Date
    val dateStr = new SimpleDateFormat().format(date)

    val lat = 25.0330
    val lon = 121.5654

    val sunInfoF = sunService.getSunInfo(lat, lon)
    val temperatureF = weatherService.getTemperature(lat, lon)

    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    val requestsF = (actorSystem.actorSelection(StatsActor.path) ? StatsActor.GetStats).mapTo[Int]

    for {
      sunInfo <- sunInfoF
      temperature <- temperatureF
      requests <- requestsF
    } yield Ok(Json.toJson(CombinedData(dateStr, sunInfo, temperature, requests)))
  }

  def users: Action[AnyContent] = Action.async {
    import actionRunner.driver.api._
    for {
      users <- actionRunner.run(userRepository.records.result)
    } yield Ok(Json.toJson(users))
  }

  def login: Action[AnyContent] = Action {
    Ok(views.html.login())
  }

  def versioned(path: String, file: Asset): Action[AnyContent] = assets.versioned(path, file)
}
