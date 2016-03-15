package com.shortstop.resizer

import java.text.SimpleDateFormat
import java.util.Date
import net.liftweb.json.{DateFormat, Formats, Serialization}
import spray.http.{HttpCharsets, HttpEntity}
import scala.language.existentials
import org.specs2.mutable.{Before, Specification}
import com.shortstop.resizer.domain.Users
import akka.actor.{ActorSystem, ActorRefFactory}
import com.shortstop.resizer.config.Configuration
import com.shortstop.resizer.rest.RestService
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest
import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple.Database.threadLocalSession
import scala.slick.driver.MySQLDriver.simple._
import slick.jdbc.meta.MTable

trait ServiceTestBase extends Specification with Specs2RouteTest with HttpService with Configuration with Before {
  args(sequential = true)

  // init Database instance
  val db = Database.forURL(url = s"jdbc:mysql://$dbHost:$dbPort/$dbName",
    user = dbUser, password = dbPassword, driver = "com.mysql.jdbc.Driver")

  val userLink = "/api/user"

  implicit def actorRefFactory: ActorSystem = system

  implicit val liftJsonFormats = new Formats {
    val dateFormat = new DateFormat {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")

      def parse(s: String): Option[Date] = try {
        Some(sdf.parse(s))
      } catch {
        case e: Exception => None
      }

      def format(d: Date): String = sdf.format(d)
    }
  }

  val spec = this

  val service = new RestService {
    override implicit def actorRefFactory: ActorRefFactory = spec.actorRefFactory
  }.rest

  /**
   * Cleans the DB before tests.
   */
  def cleanDB() = {
    // drop tables if exist
    db.withSession {
      if (MTable.getTables("users").list().nonEmpty) {
        Users.ddl.drop
        Users.ddl.create
      }
    }
  }

  implicit def HttpEntityToErrors(httpEntity: HttpEntity): Map[String, String] = {
    Serialization.read[Map[String, String]](httpEntity.asString(HttpCharsets.`UTF-8`))
  }

}