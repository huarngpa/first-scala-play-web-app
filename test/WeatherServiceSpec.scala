import com.softwaremill.macwire.wire
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import play.api.{BuiltInComponents, BuiltInComponentsFromContext, NoHttpFiltersComponents}
import services.WeatherService

class TestAppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with NoHttpFiltersComponents
  with AhcWSComponents {

  lazy val router: Router = Router.empty
  lazy val weatherService = wire[WeatherService]
}


class WeatherServiceSpec extends PlaySpec
  with OneAppPerSuiteWithComponents
  with ScalaFutures {

  override def components = new TestAppComponents(context)

  override implicit val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  "WeatherService" must {
    "return a meaningful temperature" in {
      val lat = 25.0330
      val lon = 121.5654
      val resultF = components.weatherService.getTemperature(lat, lon)
      whenReady(resultF) { res =>
        res mustBe >=(-20.0)
        res mustBe <=(60.0)
      }
    }
  }
}
