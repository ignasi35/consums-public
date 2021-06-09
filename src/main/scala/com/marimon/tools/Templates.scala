package com.marimon.tools

import com.marimon.Main.Project

object Templates {
  def indexPage(project: Seq[Project]) = {
    val list = project.map { project =>
      s"""    <li>
         |        ${project.UIName} - <a href="${project.key}-statistics.html">general stats</a><br/>
         |        <a href="${project.key}-clean-curve.html">corva</a> (<a href="${project.key}-clean-curve-log.html">log scale</a>, <a href="${project.key}-daily-curve.html">di&agrave;ria</a>)<br/>
         |        <a href="${project.key}-tariff-comparison.html">tarifes</a><br/>
         |        <a href="${project.key}-tabular-rainbow.html">tabular diari</a><br/>
         |    </li>
         |    <br/>
         |""".stripMargin
    }.mkString("<ul>\n", "\n", "</ul>\n")
    val html =
      s"""
         |<html>
         |<body>
         |$list
         |</body>
         |</html>
         |""".stripMargin
    FileTools.writeToFile(html, "site/index.html")
  }

  def cleanDailyCurve(outputName: String, csvName: String): Unit = curve(outputName, csvName, "daily-curve")

  def cleanCurve(outputName: String, csvName: String): Unit = {
    curve(outputName, csvName, "clean-curve")
    curve(outputName, csvName, "clean-curve-log", logScale = true)
  }

  private def curve(outputName: String, csvName: String, htmlName: String, logScale: Boolean = false): Unit = {
    val contents: Seq[String] = FileTools
      .readLines("templates/clean-curve.html")
      .map(
        _.replace("$$CSV_FILENAME$$", csvName)
          .replace("$$LOG_SCALE$$", logScale.toString)
      )
    FileTools.writeToFile(
      contents.mkString("\n"),
      s"site/$outputName-$htmlName.html"
    )
  }

  def tariffComparison(outputName: String, csvName: String): Unit = {
    val contents: Seq[String] = FileTools
      .readLines("templates/tariff-comparison.html")
      .map(_.replace("$$CSV_FILENAME$$", csvName))
    FileTools.writeToFile(
      contents.mkString("\n"),
      s"site/$outputName-tariff-comparison.html"
    )
  }

}