package services

import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

import model.SunInfo
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SunService(wsClient: WSClient) {
  def getSunInfo(lat: Double, lon: Double): Future[SunInfo] = {
    val sunResponseF = wsClient.url(
      "http://api.sunrise-sunset.org/json?" +
        s"lat=$lat&lng=$lon&formatted=0"
    ).get()

    sunResponseF.map { response =>
      val sunJson = response.json
      val sunriseTimeStr = (sunJson \ "results" \ "sunrise").as[String]
      val sunsetTimeStr = (sunJson \ "results" \ "sunset").as[String]
      val sunriseTime = ZonedDateTime.parse(sunriseTimeStr)
      val sunsetTime = ZonedDateTime.parse(sunsetTimeStr)
      val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

      val sunInfo = SunInfo(
        sunriseTime.format(formatter),
        sunsetTime.format(formatter)
      )
      sunInfo
    }
  }
}
