package com.marimon.tools

import scala.io.Source

/**
 *
 */
object FileTools {

  def readLines(resourceName:String): Seq[String] =
    Source
      .fromResource(resourceName)
      .getLines()
      .toSeq

}
