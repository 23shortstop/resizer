package com.shortstop.resizer.dao

import java.sql.SQLException

import com.shortstop.resizer.config.Configuration
import com.shortstop.resizer.domain.{FailureType, Failure}

import scala.slick.session.Database

/**
 * Provides functions related to database.
 */
object DataBase extends Configuration {

  // init Database instance
  private[dao] val db = Database.forURL(url = "jdbc:mysql://%s:%d/%s".format(dbHost, dbPort, dbName),
    user = dbUser, password = dbPassword, driver = "com.mysql.jdbc.Driver")

  /**
   * Produce database error description.
   *
   * @param e SQL Exception
   * @return database error description
   */
  private[dao] def databaseError(e: SQLException) =
    Failure("%d: %s".format(e.getErrorCode, e.getMessage), FailureType.DatabaseFailure)

}
