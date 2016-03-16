package com.shortstop.resizer.rest

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{ActorContext, Actor}
import akka.event.slf4j.SLF4JLogging
import com.shortstop.resizer.dao.UserDAO
import com.shortstop.resizer.domain.Failure
import net.liftweb.json.{DateFormat, Formats}
import net.liftweb.json.Serialization._
import spray.http._
import spray.routing._

/**
 * REST Service actor.
 */
class RestServiceActor extends Actor with RestService {

  implicit def actorRefFactory: ActorContext = context

  def receive = runRoute(rest ~ images)
}

/**
 * REST Service
 */
trait RestService extends HttpService with SLF4JLogging {

  val userService = new UserDAO

  implicit val executionContext = actorRefFactory.dispatcher

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

  implicit val customRejectionHandler = RejectionHandler {
    case rejections => mapHttpResponse {
      response =>
        response.withEntity(HttpEntity(ContentType(MediaTypes.`application/json`),
          write(Map("error" -> response.entity.asString))))
    } {
      RejectionHandler.Default(rejections)
    }
  }

  val rest = respondWithMediaType(MediaTypes.`application/json`) {
    path("api" / "user") {
      post {
        ctx: RequestContext =>
          handleRequest(ctx, StatusCodes.OK) {
            log.debug(s"Creating new user.")
            val user = userService.create
            user.right.map(user => Map("key" -> user.key))
          }
      }
    }
  }

  val images = respondWithMediaType(MediaTypes.`image/jpeg`) {
    path("images" / Rest) {
      directory =>
        getFromBrowseableDirectory(s"images/$directory")
    }
  }

  /**
   * Handles an incoming request and create valid response for it.
   *
   * @param ctx         request context
   * @param successCode HTTP Status code for success
   * @param action      action to perform
   */
  protected def handleRequest(ctx: RequestContext, successCode: StatusCode = StatusCodes.OK)(action: => Either[Failure, _]) {
    action match {
      case Right(result: Object) =>
        ctx.complete(successCode, write(result))
      case Left(error: Failure) =>
        ctx.complete(error.getStatusCode, net.liftweb.json.Serialization.write(Map("error" -> error.message)))
      case _ =>
        ctx.complete(StatusCodes.InternalServerError)
    }
  }
}