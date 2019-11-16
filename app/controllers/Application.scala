package controllers

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

import actors.StatsActor
import akka.actor.ActorSystem
import akka.util.Timeout
import akka.pattern.ask
import controllers.Assets.Asset
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.{SunService, WeatherService}

import scala.concurrent.ExecutionContext.Implicits.global

class Application(components: ControllerComponents, assets: Assets, ws: WSClient,
                  sunService: SunService, weatherService: WeatherService,
                  actorSystem: ActorSystem)
    extends AbstractController(components) {

  def index = Action.async {
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
    } yield  Ok(views.html.index(dateStr, sunInfo, temperature, requests))
  }

  def versioned(path: String, file: Asset) = assets.versioned(path, file)
}
