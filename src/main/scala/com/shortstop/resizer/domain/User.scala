package com.shortstop.resizer.domain

import scala.slick.driver.MySQLDriver.simple._

/**
 * A User entity.
 *
 * @param id    an unique id
 * @param key   an unique key
 */
case class User(id: Option[Long], key: String)

/**
 * A mapped users table object.
 */
object Users extends Table[User]("users") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def key = column[String]("key")

  def * = id.? ~ key <>(User, User.unapply _)

  val findById = for {
    id <- Parameters[Long]
    c <- this if c.id is id
  } yield c
}