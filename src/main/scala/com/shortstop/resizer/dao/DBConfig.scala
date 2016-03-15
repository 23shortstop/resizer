package com.shortstop.resizer.dao

import com.shortstop.resizer.config.Configuration

import scala.slick.session.Database

/**
 * Configure database
 */
object DBConfig extends Configuration {

  // init Database instance
  private[dao] val db = Database.forURL(url = "jdbc:mysql://%s:%d/%s".format(dbHost, dbPort, dbName),
    user = dbUser, password = dbPassword, driver = "com.mysql.jdbc.Driver")

}
