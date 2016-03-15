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

}