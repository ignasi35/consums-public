package com.marimon.tools

import com.marimon.HourlyMetric

import java.time.LocalDateTime


trait LineParser {
  def parse(rawLine: String): Seq[HourlyMetric]
}

object LineParsers {

  case object SomEnergiaParser extends LineParser {
    override def parse(rawLine: String): Seq[HourlyMetric] = Seq {
      val Array(_, data, hora, consume_kWh, _) = rawLine.split(";")
      val Array(d, m, y) = data.split("/").map(_.toInt)
      val dateTime = LocalDateTime.of(y, m, d, hora.toInt, 0)
      val consumWh = (BigDecimal.apply(consume_kWh.replace(",", ".")) * 1000).toInt
      HourlyMetric(dateTime, consumWh)
    }
  }

  case object EndesaParser extends LineParser {
    override def parse(rawLine: String): Seq[HourlyMetric] = Seq {
      val strings = rawLine.split(",")
      val Array(y, m, d) = strings(0).split("-").map(_.toInt)
      val Array(h, min) = strings(1).split("-")(0).split(":").map(_.toInt)
      val dateTime = LocalDateTime.of(y, m, d, h, min)
      HourlyMetric(dateTime, strings(2).toInt)
    }
  }

  case object AldroParser extends LineParser {
    override def parse(line: String): Seq[HourlyMetric] = {
      val date :: hours = line.split(",").toList
      val Array(d, m, y) = date.split("/").map(_.toInt)
      hours.take(24).zipWithIndex.map {
        case (metric, hour) => {
          val dateTime = LocalDateTime.of(y, m, d, hour, 0)
          HourlyMetric(
            dateTime,
            metric.toInt
          )
        }
      }
    }
  }

}
