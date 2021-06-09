package com.marimon.views

import com.marimon.HourlyMetric
import com.marimon.tools.{FileTools, Rainbow}

import java.time.DayOfWeek


object TabularRainbow {

  def generate(
                outputName: String,
                metrics: Seq[HourlyMetric]
              ): Unit = {

    FileTools.writeToFile(content(metrics), s"site/$outputName-tabular-rainbow.html")

  }

  private def content(metrics: Seq[HourlyMetric]): String = {
    val header =
      Seq("date", "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23")
        .map { header => s"        <th>$header</th>" }.mkString("\n")

    val rainbowMaker = Rainbow.build(metrics.map(_.consumWh))

    val rows =
      metrics
        .groupBy(_.dateTime.toLocalDate).toSeq.sortBy(_._1)
        .map { case (date, metrics) =>
          val isWeekend = date.getDayOfWeek == DayOfWeek.SATURDAY || date.getDayOfWeek == DayOfWeek.SUNDAY

          metrics.map { m => s"""        <td style="background-color:${rainbowMaker.at(m.consumWh).hex}">${m.consumWh.toString}</td>""" }
            .mkString(
              s"""        <tr><td class="${if (isWeekend) "weekend" else "weekday"}">$date</td>""",
              "\n",
              "\n         </tr>"
            )
        }

    s"""
       |<html>
       |<head>
       |    <link rel="stylesheet" href="tabular.css" />
       |</head>
       |<body>
       |<table>
       |    <tr>
       |$header
       |    </tr>
       |${rows.mkString("\n")}
       |</table>
       |</body>
       |</html>
       |
  """.stripMargin

  }
}
