package model

import play.api.libs.json.{Format, Json}

case class User(userId: String, userName: String, password: String)

object User {
  implicit val format: Format[User] = Json.format[User]
}
