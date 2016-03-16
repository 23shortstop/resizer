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
case class Resizing(id: Option[Long],
                    user: Long,
                    original: String,
                    resized: String,
                    height: Long,
                    width: Long)

/**
 * A mapped resizing requests table object.
 */
object Resizings extends Table[Resizing]("qwe") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def user = column[Long]("user")

  def original = column[String]("original")

  def resized = column[String]("resized")

  def height = column[Long]("height")

  def width = column[Long]("width")

  def * = id.? ~ user ~ original ~ resized ~ height ~ width <>(Resizing, Resizing.unapply _)

}