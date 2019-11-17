import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId, ZonedDateTime}

import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.libs.ws.ahc.AhcWSRequest
import play.api.libs.ws.{WSClient, WSResponse}
import services.SunService

import scala.concurrent.Future

class ApplicationSpec extends PlaySpec with MockitoSugar {
  "DateTimeFormat" must {
    "return 1970 as the beginning of epoch" in {
      val beginning = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneId.systemDefault())
      val formattedYear = beginning.format(DateTimeFormatter.ofPattern("YYYY"))
      formattedYear mustBe "1970"
    }
  }

  "SunService" must {
    "retrieve correct sunset and sunrise information" in {
      val wsClientStub = mock[WSClient]
      val wsRequestStub = mock[AhcWSRequest]
      val wsResponseStub = mock[WSResponse]

      val expectedResponse =
        """{
          |  "results": {
          |    "sunrise": "2019-11-16T22:11:41+00:00",
          |    "sunset": "2019-11-17T09:05:39+00:00",
          |    "solar_noon": "2019-11-17T03:38:40+00:00",
          |    "day_length": 39238,
          |    "civil_twilight_begin": "2019-11-16T21:47:35+00:00",
          |    "civil_twilight_end": "2019-11-17T09:29:45+00:00",
          |    "nautical_twilight_begin": "2019-11-16T21:19:54+00:00",
          |    "nautical_twilight_end": "2019-11-17T09:57:25+00:00",
          |    "astronomical_twilight_begin": "2019-11-16T20:52:34+00:00",
          |    "astronomical_twilight_end": "2019-11-17T10:24:46+00:00"
          |  },
          |  "status": "OK"
          |}""".stripMargin
      val jsResult = Json.parse(expectedResponse)

      val lat = 25.0330
      val lon = 121.5654
      val url = s"http://api.sunrise-sunset.org/json?lat=$lat&lng=$lon&formatted=0"

      when(wsResponseStub.json).thenReturn(jsResult)
      when(wsRequestStub.get()).thenReturn(Future.successful(wsResponseStub))
      when(wsClientStub.url(url)).thenReturn(wsRequestStub)

      val sunService = new SunService(wsClientStub)
      val resultF = sunService.getSunInfo(lat, lon)

      ScalaFutures.whenReady(resultF) { res =>
        res.sunrise mustBe "22:11:41"
        res.sunset mustBe "09:05:39"
      }
    }
  }
}
