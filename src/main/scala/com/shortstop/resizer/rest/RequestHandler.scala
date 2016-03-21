package com.shortstop.resizer.rest

import akka.event.slf4j.SLF4JLogging
import com.shortstop.resizer.config.Configuration
import com.shortstop.resizer.dao.{ResizingResultDAO, UserDAO}
import com.shortstop.resizer.domain.ResizeParameters
import com.shortstop.resizer.utils.{FileHelper, ImageHelper}

/**
 * Provides logic to handle requests.
 */
trait RequestHandler extends SLF4JLogging with Configuration {

  val userService = new UserDAO

  val resizingService = new ResizingResultDAO

  /**
   * Handles a request to resize an image.
   *
   * @param parameters  resizing parameters
   * @return            links to origin and resized images along with values of height and width
   *                    of a resized image in case of success or failure otherwise
   */
  def resize(parameters: ResizeParameters) = {
    log.debug(s"Resizing an image with parameters: $parameters")
    for {
      user <- userService.get(parameters.key).right
      userId <- userService.getId(user).right
      images <- ImageHelper.resize(parameters.image, parameters.height, parameters.width).right
      result <- resizingService.create(userId, parameters.height, parameters.width).right
    } yield {
      FileHelper.saveImage(images._1, result.original)
      FileHelper.saveImage(images._2, result.resized)
      Map(
        "original" -> s"$serviceHost:$servicePort/${result.original}",
        "resized" -> s"$serviceHost:$servicePort/${result.resized}",
        "height" -> result.height,
        "width" -> result.width
      )
    }
  }

  /**
   * Handles a request to create a new user.
   *
   * @return a unique key of a created user in case of success or failure otherwise
   */
  def createUser = {
    log.debug(s"Creating new user.")
    val user = userService.create
    user.right.map(user => Map("key" -> user.key))
  }

}
