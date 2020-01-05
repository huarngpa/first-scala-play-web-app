package helpers

import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future
import slick.jdbc.JdbcProfile

class ActionRunner(dbConfigProvider: DatabaseConfigProvider) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  val driver = dbConfig.profile

  import driver.api._

  def run[A](action: DBIO[A]): Future[A] = db.run(action)
  def runInTransaction[A](action: DBIO[A]): Future[A] = run(action.transactionally)
}
