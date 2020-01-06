package repositories

import helpers.ActionRunner
import model.User
import slick.collection.heterogeneous.HNil

import scala.concurrent.Future

class UserRepository(val actionRunner: ActionRunner) {

  import actionRunner.driver.api._

  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    def userId = column[String]("user_id", O.PrimaryKey)
    def userName = column[String]("user_name")
    def password = column[String]("password")

    def * = (userId :: userName :: password :: HNil).mapTo[User]
  }

  def records = TableQuery[UserTable]

  def getAllUsers: Future[Seq[User]] = {
    actionRunner.run(records.result)
  }

  def findByUserName(userName: String): Future[Option[User]] = {
    actionRunner.run(records.filter(_.userName === userName).result.headOption)
  }
}
