package com.marimon

import com.marimon.tools.FileTools

import java.io.File
import java.nio.file.{Files, Paths}
import java.time.LocalDateTime

object Main extends App {

  // all.csv is a single CSV with all data (multiple months or years worth
  // of data merged into a single file)
  val raw = FileTools.readLines("csv/all.csv")

  // This function parses the SomEnergia CSV format.
  // Edit this to adapt it to your format
  val somEnergiaParse: (String) => HourlyMetric = { rawLine =>
    val Array(_, data, hora, consume_kWh, _) = rawLine.split(";")
    val Array(d, m, y) = data.split("/").map(_.toInt)
    val dateTime = LocalDateTime.of(y, m, d, hora.toInt, 0)
    val consumWh = (BigDecimal.apply(consume_kWh.replace(",", ".")) * 1000).toInt
    HourlyMetric(dateTime, consumWh)
  }

  val metrics: Seq[HourlyMetric] = raw
    .filterNot(_.contains(";0;R")) // filter lines of future days (consumption==0)
    .map(somEnergiaParse)

  val tariffs: Seq[Tariff] = Tariffs.All
  val metricsPerMonth: Map[(Int, Int), Seq[HourlyMetric]] = metrics.groupBy { metric =>
    (metric.dateTime.getYear, metric.dateTime.getMonth.ordinal())
  }

  val completeReport: Seq[ReportRow] = metricsPerMonth.keySet.toSeq.sorted.flatMap {
    monthKey =>
      tariffs.map(report(metricsPerMonth(monthKey)))
  }


  private val rawReportFile = new File(new File(".").getParentFile, "site/full-report.csv")
  rawReportFile.delete()
  Files.writeString(
    Paths.get(rawReportFile.toURI),
    completeReport.map(_.toCSV).mkString("\n")
  )

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

  private val pivotReportFile = new File(new File(".").getParentFile, "site/pivot-report.csv")
  pivotReportFile.delete()
  Files.writeString(
    Paths.get(pivotReportFile.toURI),
    pivotReport
  )


  def report(metrics: Seq[HourlyMetric])(tariff: Tariff): ReportRow = {

    val highPowerContract = 4.600
    val lowPowerContract = 4.600

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
