package model

import play.api.libs.json.Json

case class CombinedData(dateStr: String, sunInfo: SunInfo, temperature: Double, requests: Int)

object CombinedData {
  implicit val writes = Json.writes[CombinedData]
}
