package com.shortstop.resizer.utils

import java.io.File

import com.sksamuel.scrimage.Image

/**
 * Provides helper methods for work with files.
 */
object FileHelper {

  /**
   * Saves a given image into a specified directory.
   * Creates additional folders if needed.
   *
   * @param image an image to store
   * @param path  a directory to store an image
   * @return
   */
  def saveImage(image: Image, path: String) = {
    createFile(path)
    image.output(path)
  }

  /**
   * Creates a new file into a specified directory.
   * Creates additional folders if needed.
   *
   * @param path a directory to create a file
   * @return
   */
  def createFile(path: String) = {
    val directory = path.take(path.lastIndexOf("/"))
    new File(directory).mkdirs()
    new File(path).createNewFile()
  }

}
