package com.marimon

sealed trait TariffPeriod

case object LowPeriod extends TariffPeriod

case object MediumPeriod extends TariffPeriod

case object HighPeriod extends TariffPeriod

case class Tariff(
                   name: String,
                   energyLow: Double, // €/kWh
                   energyMedium: Double, // €/kWh
                   energyHigh: Double, // €/kWh
                   powerLow: Double,
                   powerHigh: Double,
                   fixedCost: Int = 0) {
  private val powerLowPerHour: Double = powerLow / (365 * 24)
  private val powerHighPerHour: Double = powerHigh / (365 * 24)

  def energyCost(metric: HourlyMetric): Double = {
    val energyCost = level(metric) match { // €/kWh
      case LowPeriod => energyLow
      case MediumPeriod => energyMedium
      case HighPeriod => energyHigh
    }
    energyCost * metric.consumWh / 1000
  }

  def powerCost(lowPowerContract: Double, highPowerContract: Double)(metric: HourlyMetric): Double = {
    level(metric) match {
      case LowPeriod =>
        powerLowPerHour * lowPowerContract
      case _ =>
        powerHighPerHour * highPowerContract
    }
  }

  private def level(metric: HourlyMetric): TariffPeriod = {
    val dayOfWeek = metric.dateTime.getDayOfWeek.getValue // 1==Monday , 7==Sunday
    val hourOfDay = metric.dateTime.getHour
    val mediumHours = Set(8, 9, 14, 15, 16, 17, 22, 23)
    if (dayOfWeek == 6 || dayOfWeek == 7 || hourOfDay < 8)
      LowPeriod
    else if (mediumHours.contains(hourOfDay))
      MediumPeriod
    else
      HighPeriod
  }

}

object Tariffs {
  val SomEnergiaLegacy = Tariff("SomEnergia legacy", 0.127, 0.127, 0.127, 38.04, 38.04)
  val SomEnergia = Tariff("SomEnergia", 0.106837, 0.161527, 0.284897, 3.435303, 40.634769)
  val LuceraFixe = Tariff("Lucera Preu fixe", 0.106000, 0.160000, 0.279000, 1.812000, 39.012000, 5)
  val LucerMercatLliure = Tariff("Lucera Mercat Lliure", 0.069000, 0.131000, 0.250000, 1.812000, 39.012000, 5)
  val HolaLuz = Tariff("HolaLuz (fixe)", 0.153468, 0.218032, 0.343565, 1.796261, 38.682079)
  val Aldro = Tariff("Aldro group", 0.123848, 0.165520, 0.267203, 6.424365, 35.672545)
  val Bulb = Tariff("Bulb", 0.1774, 0.1774, 0.1774, 15.768, 44.092)
  val FactorEnergia = Tariff("Factor Energia (fixa)", 0.118096, 0.173756, 0.292941, 1.723403, 37.11312)

  val All = Seq(
    SomEnergia,
    SomEnergiaLegacy,
    LuceraFixe,
    // LucerMercatLliure,
    HolaLuz,
    // Aldro,
    // Bulb,
    FactorEnergia
  )

}
