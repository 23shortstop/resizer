package com.shortstop.resizer.utils

import java.io.IOException
import com.sksamuel.scrimage.{ImageParseException, Image}
import sun.misc.BASE64Decoder
import com.shortstop.resizer.domain.{FailureType, Failure}

/**
 * Provides methods for processing of images.
 */
object ImageHelper {

  /**
   * An instance of Base64 decoder
   */
  val decoder = new BASE64Decoder()

  /**
   * Resizes an image given as a base64 string to specified height and width.
   *
   * @param base64image an image as base64 string
   * @param height      value of height which image should have after resizing
   * @param width       value of width which image should have after resizing
   * @return            instances of origin and resized images in case of success or failure otherwise
   */
  def resize(base64image: String, height: Int, width: Int) = {
    try {
      val imageBytes = decode(base64image)
      val origin = Image(imageBytes)
      val resized = origin.scaleTo(height, width)

      Right(origin, resized)
    } catch {
      case _: IOException => Left(imageError)
      case _: ImageParseException => Left(imageError)
      case _: RuntimeException => Left(parametersError(height, width))
    }
  }

  /**
   * Decodes a base64 string to an array of bytes.
   *
   * @param base64  a string in base64 format
   * @return        an array of bytes
   */
  def decode(base64: String): Array[Byte] = decoder.decodeBuffer(base64)

  /**
   * Produce a description for an error which occurs during decoding of a base64 string.
   *
   * @return decode error description
   */
  protected def imageError =
    Failure("A base64 string can't be converted to an image.", FailureType.BadRequest)

  /**
   * Produce a description for an error which occurs during resizing of an image.
   *
   * @return parameters error description
   */
  protected def parametersError(height: Int, width: Int) =
    Failure(s"Wrong resize parameters: [height: $height; width: $width]. " +
      s"Target size must be at least 3x3", FailureType.BadRequest)

}