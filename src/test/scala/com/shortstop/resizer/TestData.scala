package com.shortstop.resizer

import java.util.UUID
import com.shortstop.resizer.domain.User

object TestData {

  val userId = 0

  val userKey = UUID.randomUUID().toString

  val testUser = User(Some(userId), userKey)

  val jpgImage = "testImages/jpg.jpg"

  val pngImage = "testImages/png.png"

  val gifImage = "testImages/gif.gif"

  val testImages = List(jpgImage, pngImage, gifImage)
}
