package com.shortstop.resizer

import java.io.{FileInputStream, File}

import sun.misc.BASE64Encoder

/**
 * Provides a Base64 encoder to use in tests.
 */
object Base64encoder {

  /**
   * An instance of encoder.
   */
  val encoder = new BASE64Encoder()

  /**
   * Encodes an image to base64 string by path to a image file.
   *
   * @param path  a path to an image to encode
   * @return      an image as a Base64 string
   */
  def encode(path: String) = {
    val file = new File(path)
    val in = new FileInputStream(file)
    val bytes = new Array[Byte](file.length.toInt)
    in.read(bytes)
    in.close()

    encoder.encode(bytes)
      .replace("\n", "")
      .replace("\r", "")
  }
}
