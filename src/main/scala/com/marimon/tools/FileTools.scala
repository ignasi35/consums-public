package com.marimon.tools

import java.io.File
import java.nio.file.{Files, Paths}
import scala.io.Source

object FileTools {

  def readLines(resourceName: String): Seq[String] =
    Source
      .fromResource(resourceName)
      .getLines()
      .toSeq

  def writeToFile(
                   content: String,
                   relativePath: String
                 ): Unit = {
    val outputFile =
      new File(
        new File(".").getParentFile,
        relativePath)
    outputFile.delete()
    Files.writeString(
      Paths.get(outputFile.toURI),
      content
    )
  }
}
