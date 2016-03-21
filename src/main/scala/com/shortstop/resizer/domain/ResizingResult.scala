package com.shortstop.resizer.domain

import scala.slick.driver.MySQLDriver.simple._

/**
 * Parameters and results of a resize request.
 *
 * @param id        an unique id
 * @param user      an id of a user who made this request
 * @param original  a link to original image
 * @param resized   a link to resized image
 * @param height    value of height which image after resizing
 * @param width     value of width which image after resizing
 */
case class ResizingResult(id: Option[Long],
                          user: Long,
                          original: String,
                          resized: String,
                          height: Int,
                          width: Int)

/**
 * A mapped resizing results table object.
 */
object ResizingResults extends Table[ResizingResult]("resizing_results") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def user = column[Long]("user")

  def original = column[String]("original")

  def resized = column[String]("resized")

  def height = column[Int]("height")

  def width = column[Int]("width")

  def * = id.? ~ user ~ original ~ resized ~ height ~ width <>(ResizingResult, ResizingResult.unapply _)

}