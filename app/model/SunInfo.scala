package model

import play.api.libs.json.{Format, Json}

case class SunInfo(sunrise: String, sunset: String)

object SunInfo {
  implicit val format: Format[SunInfo] = Json.format[SunInfo]
}
