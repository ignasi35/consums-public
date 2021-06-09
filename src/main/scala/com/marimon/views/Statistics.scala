package com.marimon.views

import com.marimon.HourlyMetric
import com.marimon.tools.FileTools

import java.time.{LocalDateTime, Month, YearMonth}

object Statistics {

  def generate(
                outputName: String,
                metrics: Seq[HourlyMetric],
                since: YearMonth = YearMonth.of(2020, Month.APRIL), // not included
                until: YearMonth = YearMonth.of(2021, Month.MAY) // not included
              ): Unit = {

    FileTools.writeToFile(content(metrics, since, until), s"site/$outputName-statistics.html")

  }

  private def content(metrics: Seq[HourlyMetric],
                      since: YearMonth,
                      until: YearMonth): String = {
    val metricsMay20April21 = metrics.filter{
      metric =>
        metric.dateTime.isBefore(until.atDay(1).atTime(0,0)) &&
        metric.dateTime.isAfter(since.atDay(since.lengthOfMonth()).atTime( 23,59))
    }
    val consumAnualkWh = metricsMay20April21.map(_.consumWh).sum/1000
    def yearMonthKey(dt:LocalDateTime) = YearMonth.of(dt.getYear, dt.getMonth)
    val consumPerMonth= metricsMay20April21
      .groupMapReduce( m => yearMonthKey(m.dateTime))(_.consumWh)(_+_)
      .toSeq.sortBy(_._1)
      .map{ case (ym, c) =>
      s"""    <li>Consum mensual ($ym): ${c/1000} kWh</li>"""
      }.mkString("\n")

    s"""
       |<html>
       |<body>
       |<br/><br/><br/><br/>
       |<ul>
       |  <li><b>Consum anual (${since.plusMonths(1)} to ${until.minusMonths(1)})</b>: $consumAnualkWh kWh</li>
       |  <ul>
       |  $consumPerMonth
       |  </ul>
       |</ul>
       |<br/><br/>
       |
       |M&agrave;xims consums horari: <br>
       | ${metrics.sortBy(_.consumWh).reverse.take(20).mkString("<ul><li>","</li><li>","</li></ul>")}
       |
       |<br/><br/>
       |</body>
       |</html>
       |
  """.stripMargin
  }
}
