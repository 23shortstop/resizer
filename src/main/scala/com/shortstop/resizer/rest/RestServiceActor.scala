package com.shortstop.resizer.rest

import akka.actor.{ActorContext, Actor}
import com.shortstop.resizer.domain.{ResizeParameters, Failure}
import net.liftweb.json.Serialization._
import spray.http._
import spray.httpx.unmarshalling._
import spray.routing._
import com.shortstop.resizer.domain.ResizeParameters._

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
trait RestService extends HttpService with RequestHandler {

  implicit val executionContext = actorRefFactory.dispatcher

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
          handleRequest(ctx) {
            createUser
          }
      }
    } ~
      path("api" / "resize") {
        post {
          entity(as[ResizeParameters]) {
            resizeParameters: ResizeParameters => {
              ctx: RequestContext =>
                handleRequest(ctx) {
                  resize(resizeParameters)
                }
            }
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