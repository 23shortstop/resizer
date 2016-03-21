package com.shortstop.resizer.domain

import java.text.SimpleDateFormat
import java.util.Date

import net.liftweb.json.Serialization._
import net.liftweb.json.{DateFormat, Formats}
import spray.http.{HttpEntity, MediaTypes}
import spray.httpx.unmarshalling.Unmarshaller

case class HistoryParameters(key: String)

/**
 * Provides implicit values for conversion of history parameters from
 * json to an instance of the HistoryParameters class.
 */
object HistoryParameters {

  implicit val liftJsonFormats = new Formats {
    val dateFormat = new DateFormat {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")

      def parse(s: String): Option[Date] = try {
        Some(sdf.parse(s))
      } catch {
        case e: Exception => None
      }

      def format(d: Date): String = sdf.format(d)
    }
  }

  implicit val HistoryParametersUnmarshaller =
    Unmarshaller[HistoryParameters](MediaTypes.`application/json`
    ) {
      case HttpEntity.NonEmpty(contentType, data) =>
        read[HistoryParameters](data.asString)
    }
}
