package com.shortstop.resizer.dao

import java.sql.SQLException
import com.shortstop.resizer.dao.DataBase._
import com.shortstop.resizer.domain._
import scala.slick.driver.MySQLDriver.simple.Database.threadLocalSession
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.meta.MTable

/**
 * Provides DAO for ResizingResult entities for MySQL database.
 */
class ResizingResultDAO {

  // create a table if not exist
  db.withSession {
    if (MTable.getTables("resizing_results").list().isEmpty) {
      ResizingResults.ddl.create
    }
  }

  /**
   * Creates and saves into db an instance of ResizingResult.
   *
   * @param userId an id of user who made request for resizing
   * @param height value of image height after resizing
   * @param width  value of image width after resizing
   * @return
   */
  def create(userId: Long, height: Int, width: Int): Either[Failure, ResizingResult] = {
    try {
      val resizingResult = ResizingResult(None, userId, "", "", height, width)
      val id = db.withSession {
        ResizingResults returning ResizingResults.id insert resizingResult
      }
      Right(resizingResult.copy(
        id = Some(id),
        original = s"images/$userId/origin/$id",
        resized = s"images/$userId/resized/$id"))
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  /**
   * Returns resizing history of a user with specified id.
   *
   * @param userId a user id.
   * @return
   */
  def getHistory(userId: Long): Either[Failure, Vector[ResizingResult]] = {
    try {
      db.withTransaction {
        val query = ResizingResults.where(_.user === userId)
        val results: Vector[ResizingResult] = query.run.asInstanceOf[Vector[ResizingResult]]
        Right(results)
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }
}
