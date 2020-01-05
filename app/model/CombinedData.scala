package model

import play.api.libs.json.{Format, Json}

case class CombinedData(dateStr: String, sunInfo: SunInfo, temperature: Double, requests: Int)

object CombinedData {
  implicit val format: Format[CombinedData] = Json.format[CombinedData]
}
