package services

import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import java.util.{Base64, UUID}

import model.User
import org.mindrot.jbcrypt.BCrypt
import play.api.cache.SyncCacheApi
import play.api.mvc.Cookie
import repositories.UserRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class AuthService(cacheApi: SyncCacheApi, userRepository: UserRepository) {

  val mda: MessageDigest = MessageDigest.getInstance("SHA-512")
  val cookieHeader = "X-Auth-Token"

  def login(userName: String, password: String): Future[Option[Cookie]] = {
    for {
      maybeUser <- checkUser(userName, password)
      maybeCookie = maybeUser.map(createCookie)
    } yield maybeCookie
  }

  private def checkUser(userName: String, password: String): Future[Option[User]] = {
    userRepository.findByUserName(userName).map {
      case Some(user) if BCrypt.checkpw(password, user.password) => Some(user)
      case _ => None
    }
  }

  private def createCookie(user: User): Cookie = {
    val randomPart = UUID.randomUUID().toString
    val userPart = user.userId
    val key = s"$randomPart|$userPart"
    val token = Base64.getEncoder.encodeToString(mda.digest(key.getBytes))
    val duration = Duration.create(10, TimeUnit.HOURS)
    cacheApi.set(token, user, duration)
    Cookie(cookieHeader, token, maxAge = Some(duration.toSeconds.toInt))
  }
}
