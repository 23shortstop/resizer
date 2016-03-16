package com.shortstop.resizer.domain

/**
 * Resize parameters.
 *
 * @param key     an unique user key
 * @param image   an byte area represented as string
 * @param height  value of height which image should have after resizing
 * @param width   value of width which image should have after resizing
 */
case class ResizeParameters(key: Option[String] = None,
                            image: Option[String] = None,
                            height: Option[Long] = None,
                            width: Option[Long] = None)
