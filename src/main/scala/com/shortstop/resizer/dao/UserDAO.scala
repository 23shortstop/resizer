package com.shortstop.resizer.dao

import java.util.UUID

import com.shortstop.resizer.dao.DataBase._
import com.shortstop.resizer.domain._
import java.sql._
import scala.slick.driver.MySQLDriver.simple.Database.threadLocalSession
import scala.slick.driver.MySQLDriver.simple._
import slick.jdbc.meta.MTable

/**
 * Provides DAO for User entities for MySQL database.
 */
class UserDAO {

  // create a table if not exist
  db.withSession {
    if (MTable.getTables("users").list().isEmpty) {
      Users.ddl.create
    }
  }

  /**
   * Creates a user entity and saves it into a database.
   *
   * @return a saved user entity
   */
  def create: Either[Failure, User] = {
    try {
      val key = UUID.randomUUID().toString
      val user = User(None, key)
      val id = db.withSession {
        Users returning Users.id insert user
      }
      Right(user.copy(id = Some(id)))
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  /**
   * Retrieves a user from database by specified key.
   *
   * @param key a key of the user to retrieve
   * @return user entity with specified id
   */
  def get(key: String): Either[Failure, User] = {
    try {
      db.withSession {
        Users.findByKey(key).firstOption match {
          case Some(user: User) =>
            Right(user)
          case _ =>
            Left(notFoundError(key))
        }
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  /**
   * Returns an id of specified user.
   *
   * @param user  an instance of a user
   * @return      an id of of specified user
   */
  def getId(user: User) = {
    try {
      Right(user.id.get)
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  /**
   * Produces user not found error description.
   *
   * @param userKey id of the customer
   * @return not found error description
   */
  protected def notFoundError(userKey: String) =
    Failure(s"User with id=$userKey does not exist", FailureType.NotFound)

}