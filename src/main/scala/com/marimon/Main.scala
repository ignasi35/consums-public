package com.marimon

import com.marimon.tools.MetricsReaders.SomEnergiaReader
import com.marimon.tools.{MetricsReader, Templates}
import com.marimon.views.{CleanCurve, Statistics, TabularRainbow, TariffComparison}

import java.time.{Month, YearMonth}

object Main extends App {

  def analyse(p: Project): Unit = {
    CleanCurve.generate(p.key, p.metrics)
    CleanCurve.generateByDay(p.key, p.metrics)
    TariffComparison.generate(p.key, p.metrics, p.lowPower, p.highPower)
    TabularRainbow.generate(p.key, p.metrics)
    Statistics.generate(p.key, p.metrics, p.since, p.until)
  }

  /**
   * @param UIName used as a display name
   * @param key unique ID. Name of the subflder under `resources/csv/$key$` with data. Also
   *            used to produce unique output files (CSV, HTML, etc...)
   * @param reader an implementation that can read the CSV/XLS/etc files from `resources/csv/$key$`
   *               into [[HourlyMetric]]
   * @param lowPower contratcted power for cheap periods (nights and weekends)
   * @param highPower contratcted power for expensive periods (weekdays during daylight)
   * @param resourceName name of the file to load from `resources/csv/$key$`
   * @param since starting [[YearMonth]] to generate the general Statistics
   * @param until ending [[YearMonth]] to generate the general Statistics (prefer this to be a year after [[since]]
   */
  case class Project(
                      UIName: String,
                      key: String,
                      reader: MetricsReader,
                      lowPower: Double,
                      highPower: Double,
                      resourceName: String = "all.csv",
                      since: YearMonth = YearMonth.of(2020, Month.APRIL), // not included
                      until: YearMonth = YearMonth.of(2021, Month.MAY) // not included
                    ) {
    val metrics: Seq[HourlyMetric] = reader.read(key, resourceName)
  }

  private val sampleHouse = Project("Example House Name 2020-2021", "my-project-key", SomEnergiaReader, 4.6, 4.6)
  private val names: Seq[Project] = Seq(
    sampleHouse
  )
  names.foreach(analyse)
  Templates.indexPage(names)
}