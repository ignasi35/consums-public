package com.marimon.views

import com.marimon.HourlyMetric
import com.marimon.tools.{FileTools, Templates}

import java.time.LocalDate

object IsoFormatters {
  def toIso(row: HourlyMetric): String = s"${row.ISO},${row.consumWh}"

  def toIso(t: (LocalDate, Int)): String = s"${t._1.toString},${t._2}"
}

object CleanCurve {

  import IsoFormatters._

  def generate(
                outputName: String,
                metrics: Seq[HourlyMetric],
              ): Unit = {
    val content = metrics.map(toIso).mkString("\n")
    val filename = s"$outputName-clean.csv"
    FileTools.writeToFile(content, s"site/$filename")
    Templates.cleanCurve(outputName, filename)
  }

  def generateByDay(
                     outputName: String,
                     metrics: Seq[HourlyMetric],
                   ): Unit = {
    val content = metrics
      .groupBy(_.dateTime.toLocalDate)
      .view.mapValues { metrics => HourlyMetric(metrics.head.dateTime, metrics.map(_.consumWh).sum) }.toMap.values
      .map(toIso)
      .mkString("\n")
    val filename = s"$outputName-by-day.csv"
    FileTools.writeToFile(content, s"site/$filename")
    Templates.cleanDailyCurve(outputName, filename)
  }

}
