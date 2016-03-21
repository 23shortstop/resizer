package com.shortstop.resizer

import java.io.File
import java.util.UUID

import com.sksamuel.scrimage.Image
import spray.http.HttpEntity
import spray.http.HttpRequest

import com.shortstop.resizer.domain.{ResizeParameters, Users}
import net.liftweb.json.Serialization
import spray.http.HttpMethods._
import spray.http._
import spray.http.StatusCodes._
import TestData._

import scala.slick.driver.MySQLDriver.simple.Database.threadLocalSession
import scala.util.Random

class ServiceSpec extends ServiceTestBase {
  //db for tests sets in application.conf
  //if the specified db does not exist, then it should be created.

  def before = {
    cleanDB()
    saveUser
  }

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

    "resize images" in {
      testImages.map { image =>
        val base64 = Base64encoder.encode(image)
        val requestHeight = 50
        val requestWidth = 100
        HttpRequest(
          method = POST,
          uri = resizeLink,
          entity = HttpEntity(ContentType(MediaTypes.`application/json`),
            Serialization.write(ResizeParameters(userKey, base64, requestHeight, requestWidth)))) ~> service ~> check {
          response.status should be equalTo OK
          response.entity should not be equalTo(None)

          val responseMap = responseAs[Map[String, String]]

          val origin = responseMap.get("original")
          origin should not be equalTo(None)
          origin.get.contains(s"/images/$userId/origin/")

          val resized = responseMap.get("resized")
          resized should not be equalTo(None)
          resized.get.contains(s"/images/$userId/resized/")

          val resizedFile = new File(resized.get.dropWhile(_ != '/').drop(1))
          val resizedImage = Image.fromFile(resizedFile)
          resizedImage.height === requestHeight
          resizedImage.width === requestWidth

          val height = responseMap.get("height")
          height should not be equalTo(None)
          height.get.toInt should be equalTo requestHeight

          val width = responseMap.get("width")
          width should not be equalTo(None)
          width.get.toInt should be equalTo requestWidth

          val originFile = new File(origin.get.dropWhile(_ != '/').drop(1))
          resizedFile.delete() === true
          originFile.delete() === true
        }
      }
    }

    "return \"Not found\" error if a wrong user key was provided" in {
      val base64 = Base64encoder.encode(jpgImage)
      val requestHeight = 50
      val requestWidth = 100
      val randomId = UUID.randomUUID().toString
      HttpRequest(
        method = POST,
        uri = resizeLink,
        entity = HttpEntity(ContentType(MediaTypes.`application/json`),
          Serialization.write(ResizeParameters(randomId, base64, requestHeight, requestWidth)))
      ) ~> service ~> check {
        response.status should be equalTo NotFound
        response.entity should not be equalTo(None)
        responseAs[Map[String, String]].get("error") ===
          Some(s"User with id=$randomId does not exist")
      }
    }

    "return \"Bad request\" error if wrong parameters were provided" in {
      val base64 = Base64encoder.encode(jpgImage)
      val requestHeight = 0
      val requestWidth = 0
      HttpRequest(
        method = POST,
        uri = resizeLink,
        entity = HttpEntity(ContentType(MediaTypes.`application/json`),
          Serialization.write(ResizeParameters(userKey, base64, requestHeight, requestWidth)))
      ) ~> service ~> check {
        response.status should be equalTo BadRequest
        response.entity should not be equalTo(None)
        responseAs[Map[String, String]].get("error") ===
          Some(s"Wrong resize parameters: [height: $requestHeight; width: $requestWidth]. " +
            s"Target size must be at least 3x3")
      }
    }

    "return \"Bad request\" error if base64 string can't be converted to an image" in {
      val base64 = Random.nextString(100)
      val requestHeight = 100
      val requestWidth = 50
      HttpRequest(
        method = POST,
        uri = resizeLink,
        entity = HttpEntity(ContentType(MediaTypes.`application/json`),
          Serialization.write(ResizeParameters(userKey, base64, requestHeight, requestWidth)))
      ) ~> service ~> check {
        response.status should be equalTo BadRequest
        response.entity should not be equalTo(None)
        responseAs[Map[String, String]].get("error") ===
          Some("A base64 string can't be converted to an image.")
      }
    }
  }
}