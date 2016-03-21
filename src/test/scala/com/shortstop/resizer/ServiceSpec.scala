package com.shortstop.resizer

import com.shortstop.resizer.domain.Users
import spray.http.HttpEntity
import spray.http.HttpMethods._
import spray.http.StatusCodes._
import spray.http.HttpRequest
import scala.slick.driver.MySQLDriver.simple.Database.threadLocalSession

class ServiceSpec extends ServiceTestBase {
  //db for tests sets in application.conf
  //if the specified db does not exist, then it should be created.

  def before = cleanDB()

  "Service" should {
    "create users" in {
      HttpRequest(POST, userLink, entity = HttpEntity("")) ~> service ~> check {

        response.status should be equalTo OK
        response.entity should not be equalTo(None)
        val key = responseAs[Map[String, String]].get("key")
        key should not be equalTo(None)
        db.withSession {
          val user = key.flatMap(Users.findByKey(_).firstOption)
          user should not be equalTo(None)
          user.map(_.id.get).getOrElse(0L) must be greaterThan 0L
        }
      }
    }
  }
}