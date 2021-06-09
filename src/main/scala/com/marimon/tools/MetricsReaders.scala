package com.marimon.tools

import com.marimon.HourlyMetric
import com.marimon.tools.LineParsers.{AldroParser, EndesaParser, SomEnergiaParser}

sealed trait MetricsReader {
  def read(resourceGroup: String,
           resourceName: String): Seq[HourlyMetric]
}

object MetricsReaders {

  case object SomEnergiaReader extends MetricsReader {
    def read(resourceGroup: String,
             resourceName: String): Seq[HourlyMetric] = {
      val raw: Seq[String] =
        FileTools.readLines(s"csv/$resourceGroup/$resourceName")
          .filterNot(_.contains(";0;R"))

      raw.flatMap(SomEnergiaParser.parse)
    }
  }

  case object EndesaReader extends MetricsReader {
    def read(resourceGroup: String,
             resourceName: String): Seq[HourlyMetric] = {
      val raw: Seq[String] =
        FileTools.readLines(s"csv/$resourceGroup/$resourceName")
          // there's a bug and a date includes the time range "24:00-25:00"
          .filter(_.indexOf(",24:00") == -1)
          .sorted

      raw.flatMap(EndesaParser.parse)
    }
  }

  case object AldroReader extends MetricsReader {
    def read(resourceGroup: String,
             resourceName: String): Seq[HourlyMetric] = {
      FileTools
        .readLines(s"csv/$resourceGroup/$resourceName")
        .flatMap(AldroParser.parse)
    }
  }


}
