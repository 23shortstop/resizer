package com.shortstop.resizer.domain

import spray.http.{StatusCodes, StatusCode}

/**
 * Service failure description.
 *
 * @param message   error message
 * @param errorType error type
 */
case class Failure(message: String, errorType: FailureType.Value) {

  /**
   * Return corresponding HTTP status code for failure specified type.
   *
   * @return HTTP status code value
   */
  def getStatusCode: StatusCode = {
    FailureType.withName(this.errorType.toString) match {
      case FailureType.BadRequest => StatusCodes.BadRequest
      case FailureType.DatabaseFailure => StatusCodes.InternalServerError
      case _ => StatusCodes.InternalServerError
    }
  }
}

/**
 * Allowed failure types.
 */
object FailureType extends Enumeration {
  type Failure = Value

  val BadRequest = Value("bad_request")
  val DatabaseFailure = Value("database_error")
  val InternalError = Value("internal_error")
}

