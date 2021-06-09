package com.marimon.views

import com.marimon.tools.{FileTools, Templates}
import com.marimon.{HourlyMetric, Tariff, Tariffs}

object TariffComparison {

  def generate(
                outputName: String,
                metrics: Seq[HourlyMetric],
                lowPowerContract: Double,
                highPowerContract: Double
              ): Unit = {

    val tariffs: Seq[Tariff] = Tariffs.All
    val metricsPerMonth: Map[(Int, Int), Seq[HourlyMetric]] = metrics.groupBy { metric =>
      (metric.dateTime.getYear, metric.dateTime.getMonth.ordinal())
    }

    val completeReport: Seq[ReportRow] = metricsPerMonth.keySet.toSeq.sorted.flatMap {
      monthKey =>
        tariffs.map(report(metricsPerMonth(monthKey), lowPowerContract, highPowerContract))
    }

    val pivotReport: String = {
      val byMonth = completeReport.groupBy(_.rowKey)
      val tariffNames = Tariffs.All.map(_.name).sorted
      val header = "monthkey;" + tariffNames.mkString(";")
      val values = byMonth.keySet.toSeq.sorted.map { monthKey =>
        val costsEachMonth = {
          val costsByName = byMonth(monthKey).groupBy(_.tariff.name)
          tariffNames.map(costsByName).map(_.head.monthlyCost)
        }.mkString(";")
        s"$monthKey;${costsEachMonth}"
      }.mkString("\n")
      s"$header\n$values"
    }

    val filename = s"$outputName-pivot-report.csv"
    FileTools.writeToFile(pivotReport, s"site/$filename")
    Templates.tariffComparison(outputName, filename)
  }

  def report(metrics: Seq[HourlyMetric],
             lowPowerContract: Double,
             highPowerContract: Double
            )(tariff: Tariff): ReportRow = {

    val hourlyEnergyCost = metrics.map(tariff.energyCost)
    val costEnergy: Double = hourlyEnergyCost.sum
    val hourlyPowerCost = metrics.map(tariff.powerCost(lowPowerContract, highPowerContract))
    val costPower: Double = hourlyPowerCost.sum
    val metric = metrics.head
    ReportRow(tariff, metric, costEnergy, costPower)
  }

  case class ReportRow(tariff: Tariff, metric: HourlyMetric, costEnergy: Double, costPower: Double) {
    private val tariffName = tariff.name
    private val year = metric.dateTime.getYear
    // month in 1-base ordinals for presentation
    private val month = metric.dateTime.getMonth.ordinal() + 1
    private val fixedCost = tariff.fixedCost
    val monthlyCost = fixedCost + costEnergy + costPower
    val rowKey = f"$year/$month%02d"

    def toCSV =
      Array(
        tariffName,
        year,
        month,
        fixedCost,
        costEnergy,
        costPower,
        monthlyCost)
        .mkString(";")
  }
}
