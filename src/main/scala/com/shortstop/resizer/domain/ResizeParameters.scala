package com.shortstop.resizer.domain

import java.text.SimpleDateFormat
import java.util.Date

import net.liftweb.json.{DateFormat, Formats}
import spray.http.{HttpEntity, MediaTypes}
import spray.httpx.unmarshalling.Unmarshaller
import net.liftweb.json.Serialization._

/**
 * Resize parameters.
 *
 * @param key     an unique user key
 * @param image   an byte area represented as string
 * @param height  value of height which image should have after resizing
 * @param width   value of width which image should have after resizing
 */
case class ResizeParameters(key: String,
                            image: String,
                            height: Int,
                            width: Int)

/**
 * Provides implicit values for conversion of resize parameters from
 * json to an instance of the ResizeParameters class.
 */
object ResizeParameters {

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

  implicit val ResizeParametersUnmarshaller =
    Unmarshaller[ResizeParameters](MediaTypes.`application/json`
    ) {
      case HttpEntity.NonEmpty(contentType, data) =>
        read[ResizeParameters](data.asString)
    }
}
